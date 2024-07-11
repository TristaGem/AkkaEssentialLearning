package part2
import akka.actor.{Actor, ActorRef, ActorSystem, Props}


object CounterExample extends App {
  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    import Counter._

    override def receive: Receive = countReceive(0)
    def countReceive(count: Int): Receive = {
      case Increment => context.become(countReceive(count+1))
      case Decrement => context.become(countReceive(count-1))
      case Print => println(s"[counter] My current count is $count")
    }
  }
  import Counter._
  val system = ActorSystem("CounterDemo")
  val counter = system.actorOf(Props[Counter], "myCounter")

  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print

}
