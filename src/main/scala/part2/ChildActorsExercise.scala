package part2

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorsExercise extends App {
  object WordCounterMaster {
    case class Initialize(nChildren: Int)

    case class WordCountTask( sender: ActorRef, text: String)

    case class WordCountReply(/* TODO */ sender: ActorRef, text: String, count: Int)
  }

  //Distributed word counting
  class WordCounterMaster extends Actor {
    import WordCounterMaster._

    override def receive: Receive = {
      case Initialize(n) =>
        val keys = 0 until n
        val actors = keys.map(_ => context.actorOf(Props[WordCounterWorker]))
        val map = keys.zip(actors).toMap
        context.become(countWords(0, n, map))
    }

    def countWords(idx: Int, total: Int, actors: Map[Int, ActorRef]): Receive = {
      case WordCountTask(sender, message) =>
        actors(idx) ! WordCountTask(sender, message)
        context.become(countWords((idx+1) % total, total, actors))
      case WordCountReply(sender, text, count) =>
        sender ! WordCountReply(sender, text, count)
//        sender() ! WordCountReply(count)

    }
  }

  class WordCounterWorker extends Actor {
    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(sender, message) =>
        context.sender() ! WordCountReply(sender, message, message.split(" ").length)
      case _ =>
    }
  }
// /*
  //create WordCounterMaster
  //send Initialize(10) to wordCounterMaster
  //send "Akka is awesome" to wordCounterMaster
  //wcm will send a WordCountTask("...") to one of its children child replies with a WordCountReply(3) to the master
  //master replies with 3 to the sender.
  //
  //requester -> wcm -> WCW
  //requester <- wcm <-
  //
  //*/ round robin logic ¡ 1,2,3,4,5 and 7 tasks // 1,2,3,4,5,1,2
  import WordCounterMaster._
 val system = ActorSystem("ParentChildDemo")
  val wcm = system.actorOf(Props[WordCounterMaster], "master")
  class Requester extends Actor {
    override def receive: Receive = {
      case WordCountTask(sender, message) =>
        wcm ! WordCountTask(sender, message)
      case WordCountReply(sender, message, count) =>
    }
  }
  wcm ! Initialize(5)

  val requester = system.actorOf(Props[Requester], "requester")

  requester ! WordCountTask(requester, "hello world")
  wcm ! WordCountTask("gents, how are you doing")
  wcm ! WordCountTask("generally speaking, this is stupid")

}
