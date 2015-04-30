package clesa.ha.actors

import akka.actor.{ActorRef, Actor}
import clesa.ha.components.Hue
import clesa.ha.events.linuxinput.Wheel
import collection.JavaConversions._

class HueActor(broadcastActor: ActorRef, ipAddress: String)
  extends Actor {

  val hue = new Hue(ipAddress, "clesa", "ha", "newdeveloper")

  def receive = {
    case te: Wheel => {
      println("Received Wheel!")
      hue.increaseBrightnessByPercent(hue.allLights.head, te.value * 5)
    }
    case other => println(s"don't know what to do with anything but a wheel :(" + other)
  }
}

object HueActor {
  val name = "HueActor"
}
