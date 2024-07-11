package part2
import akka.actor.{Actor, ActorContext, ActorRef, ActorSystem, Props}

import scala.util.{Failure, Success, Try}


object BankAccount extends App

  class BankAccountActor extends Actor {
    var cur = 0.0
    override def receive: Receive = {
      case AdminMessage(request, ref) => {
        ref ! request
      }
      case DepositRequest(m) => {
        cur += m
        sender() ! s"current $self balance is $cur"
      }
      case WithdrawRequest(m) => Try {
        if (m > cur) {
          Failure(new RuntimeException(s"Trying to withdraw more than the balance $cur from $self"))
        } else {
          cur -= m
          Success(s"After deducting $m, current balance is $cur")
        }
      }
      case Statement() => {
        sender() ! s"current $self balance is $cur"
      }
      case m: String => println(s"$self receive message: $m")
//      case result: Try[_] => {
//        case Success(m) => println(m)
//        case Failure(exception) => println(s"failed with exception like, $exception")
//      }
    }
  val system = ActorSystem("actorCapabilitiesDemo")
  val alice = system.actorOf(Props[BankAccountActor], "alice")
  val admin = system.actorOf(Props[BankAccountActor], "admin")

  case class DepositRequest(amount: Double)
  case class WithdrawRequest(amount: Double)
  case class Statement()

  case class AdminMessage[T](m: T, ref: ActorRef)
//  case class ForwardMessage()

//  alice ! DepositRequest(10)
//  alice ! WithdrawRequest(1)
//  alice ! WithdrawRequest(11)
  admin ! AdminMessage(Statement(), alice)
  admin ! AdminMessage(DepositRequest(10), alice)
  admin ! AdminMessage(WithdrawRequest(9), alice)

//  admin ! AdminMessage(WithdrawRequest(2), alice)
}
