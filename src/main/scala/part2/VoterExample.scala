package part2
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.collection.mutable

object VoterExample extends App{

  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate: Option[String])
  class Citizen extends Actor {
    override def receive: Receive = voteHandler("")
    def voteHandler(choice: String): Receive = {
      case Vote(candidate: String) =>
        context.become(voteHandler(candidate))
      case VoteStatusRequest =>
        if(choice == "") sender() ! VoteStatusReply(None)
        else {
          sender() ! VoteStatusReply(Some(choice))
        }
    }
  }

  case class AggregateVotes(citizens: Set[ActorRef])
  class VoteAggregator extends Actor {
    override def receive: Receive = countVote(Set(), new mutable.HashMap[String, Int]())

    def countVote(stillWaiting: Set[ActorRef], voteMap: mutable.HashMap[String, Int]): Receive = {
      case AggregateVotes(citizens: Set[ActorRef]) =>
        for (elem <- citizens) {
          elem ! VoteStatusRequest
        }
        context.become(countVote(citizens, voteMap))
      case VoteStatusReply(None) =>
        sender() ! VoteStatusRequest
      case VoteStatusReply(Some(candidate)) =>
        voteMap.put(candidate, voteMap.getOrElse(candidate, 0)+1)
        val newStillWaiting = stillWaiting - sender()
        context.become(countVote(newStillWaiting, voteMap))
        if(newStillWaiting.isEmpty)
          println(s"[aggregator] poll stats: $voteMap")
    }
  }
  val system = ActorSystem("CounterDemo")
  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])
  val voteAggregator = system.actorOf(Props[VoteAggregator])

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")


  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, daniel))

}
