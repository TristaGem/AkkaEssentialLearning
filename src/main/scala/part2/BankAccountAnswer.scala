package part2

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorContext, ActorRef, ActorSystem, Props}
import part2.BankAccountAnswer.system

object BankAccountAnswer extends App {

  // bank account
  object BankAccount {
    case class Deposit(amount: Int)

    case class Withdraw(amount: Int)

    case object Statement

    case class TransactionSuccess(message: String)

    case class TransactionFailure(reason: String)
  }

  class BankAccount extends Actor {

    import BankAccount._

    var funds = 0

    override def receive: Receive = {
      case Deposit(amount) => if (amount < 0) sender() ! TransactionFailure("invalid deposit amount")
      else {
        funds += amount
        sender() ! TransactionSuccess(s"successfully deposited $amount")
      }

      case Withdraw(amount) =>
        if (amount < 0) sender() ! TransactionFailure("invalid withdraw amount")
        else if (amount > funds) sender() ! TransactionFailure("insufficient funds")
        else
          funds -= amount
        sender() ! TransactionSuccess(s"successfully withdrew $amount")

      case Statement => sender() ! s"Your balance is $funds"
    }

  }

  object Person {
    case class LiveTheLife(account: ActorRef)
  }

  class Person extends Actor {

    import Person._
    import BankAccount._

    override def receive: Receive = {
      case LiveTheLife(account) =>
        (0 to 1000).foreach(_ => {
          account ! Deposit(100)
          account ! Withdraw(100)
        })
      case message => println(message.toString)
    }
  }
  import Person._
  val system = ActorSystem("actorCapabilitiesDemo")
  val account = system.actorOf(Props[BankAccount], "bankAccount")
  val person = system.actorOf(Props[Person], "billionaire")

  person ! LiveTheLife(account)

}
