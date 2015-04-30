package clesa.ha.actors

import akka.actor.{ActorRef, Actor}
import clesa.ha.events.Event

class BroadcastActor
  extends Actor {

  var knownActors = Seq[ActorRef]()

  def receive = {
    case ar: ActorRef => {
      knownActors = knownActors ++ Seq(ar)
    }
    case event: Event => {
      knownActors.foreach(_ ! event)
    }
    case other =>
  }
}

object BroadcastActor {
  val name = "BroadcastActor"
}
