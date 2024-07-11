package part2

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App{
  //part1 - actor systems, one actors system per application
  val actorSystem = ActorSystem("firstSystem")
  println(actorSystem.name)

  // part 2 - create actors
   // word count actor
  class WordCountActor extends Actor {
    // internal data
    var totalWords = 0

    // behavior
    def receive: PartialFunction[Any, Unit] = {
      case message: String =>
        println(s" [word counter] I have received: $message")
        totalWords += message.split(" "). length
      case msg => println(s" [word counter] I cannot understand ${msg.toString}")
    }
  }
  // part3 - instantiate our actor I
  val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter")
  val anotherWordCounter = actorSystem.actorOf(Props[WordCountActor], "anotherWordCounter")

  // part4 communicate!
  wordCounter ! "I am learning Akka and it's pretty damn cool!" // "tell"
  anotherWordCounter ! "A different message"


  object Person {
    def props(name: String) = Props(new Person(name))
  }

  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case "hi" => println(s"Hi, my name is $name")
      case _ =>
    }
  }

  val person = actorSystem.actorOf(Person.props("Bob"))
  person ! "hi"
}