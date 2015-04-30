package clesa.ha.actors

import java.util.concurrent.Executors
import akka.actor.{ActorRef, Actor}
import clesa.ha.components.LinuxInputRunnable

class LinuxInputActor(broadcastActor: ActorRef, eventFilePath: String)
  extends Actor {

  val es = Executors.newFixedThreadPool(1)

  es.submit(new LinuxInputRunnable(eventFilePath, lie => broadcastActor ! lie))

  def receive = {
    case _ => //nothing to do... could I interest you in creating a LinuxInputEvent for everyone?
  }

  def shutdown(): Unit = {
    es.shutdown()
  }
}

object LinuxInputActor {
  val name = "LinuxInputActor"
}