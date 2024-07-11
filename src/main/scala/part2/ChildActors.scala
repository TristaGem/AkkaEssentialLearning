package part2

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActors extends App{
  // Actors can create other actors
  val system = ActorSystem("ParentChildDemo")

//  /1 Actors can create other actors

  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }
  class Parent extends Actor {

    import Parent._

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating child")
        // create a new actor right HERE
        val childRef = context.actorOf(Props[Child], name)
        context.become(withChild(childRef))
    }

    def withChild(childRef: ActorRef): Receive = {
      case TellChild (message) =>
      if (childRef != null) childRef forward message
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"${self.path} I got: $message")
    }
  }

  import Parent._
  val parent = system.actorOf(Props [Parent], "parent")
  parent ! CreateChild("child")
  parent ! TellChild("hey Kid!")



}
