package part2

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChangingActorBehaviour extends App   {
  object FussyKid {
    val SAD = "SAD"
    val HAPPY = "HAPPY"
    case object KidAccept
    case object KidReject
  }

  class FussyKid extends Actor {

    import FussyKid._
    import Mom._

    //    /1 internal state of the kid
    var state = HAPPY

    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>
        if (state == HAPPY) sender() ! KidAccept
        else {
          sender() ! KidReject
        }
    }
  }

  object Mom {
    case class MomStart(kidRef: ActorRef)
    case class Food(food: String)
    case class Ask(message: String)
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }

  class Mom extends Actor {
    import Mom._
    import FussyKid._

    override def receive: Receive = {
      case MomStart(kidRef) =>
        // test our interaction
        kidRef ! Food(VEGETABLE)
        kidRef ! Ask("do you want to play?")
      case KidAccept => println("Yay, my kid is happy!")
      case KidReject => println("My kid is sad, but as he's healthy!")
    }
  }
  import Mom._
  val system = ActorSystem("changingActorBehaviorDemo")
  val fussyKid = system.actorOf(Props[FussyKid])
  val mom = system.actorOf(Props[Mom])
  mom ! MomStart(kidRef = fussyKid)

  class StatelessFussyKid extends Actor {
    import FussyKid._
    import Mom._
    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.unbecome()// change my receive handler to sadReceive
      case Food(CHOCOLATE) => context.become(happyReceive, false)
      case Ask(_) => sender() ! KidAccept
    }

      def sadReceive: Receive = {
        case Food(VEGETABLE) =>
          context.become(sadReceive, false)
        case Food(CHOCOLATE) =>
          context.unbecome() // stay sad case Food(CHOCOLATE) context.become(happyReceive) V/ change my receive handler to happyReceive
        case Ask(_) => sender() ! KidReject
      }
    }

  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid])
  mom ! MomStart(statelessFussyKid)
}
