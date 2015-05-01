package clesa.ha.actors

import akka.actor.{ActorRef, Actor}
import clesa.ha.components.Hue
import clesa.ha.events.hue.HueEvent
import clesa.ha.events.linuxinput.Wheel
import com.philips.lighting.model.PHHueError
import collection.JavaConversions._

class HueActor(broadcastActor: ActorRef, ipAddress: String)
  extends Actor {

  var stateKnown = true

  val hue = new Hue(hueEvent => broadcastActor ! hueEvent,
                    hueError => self ! hueError,
                    ipAddress,
                    "clesa",
                    "ha",
                    "newdeveloper")

  def receive = {
    case te: Wheel => {
      println("Received Wheel!")
      if(stateKnown) stateKnown = !hue.increaseBrightnessBy(hue.allLights.head, te.value * 10)
    }
    case he: HueEvent => {
      println("Received Hue Event!")
      hue.updateLightState(he)
      stateKnown = true
    }
    case hError: PHHueError => {
      println("Received Error Event!")
      stateKnown = true
    }
    case other => println(s"don't know what to do with anything but a wheel :(" + other)
  }
}

object HueActor {
  val name = "HueActor"
}
