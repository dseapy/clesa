package clesa.ha.actors

import akka.actor.{ActorRef, Actor}
import clesa.ha.components.Hue
import clesa.ha.events.hue.HueEvent
import clesa.ha.events.linuxinput.Wheel
import com.philips.lighting.model.PHHueError
import collection.JavaConversions._
import scala.collection.mutable

class HueActor(broadcastActor: ActorRef, ipAddress: String)
  extends Actor {

  var stateKnown = true

  val wheelQueue = new mutable.Queue[Wheel]()

  val hue = new Hue(hueEvent => broadcastActor ! hueEvent,
                    hueError => self ! hueError,
                    ipAddress,
                    "clesa",
                    "ha",
                    "newdeveloper")

  def receive = {
    case te: Wheel => {
      println("Received Wheel!")
      if(stateKnown) stateKnown = !hue.increaseBrightnessBy(hue.allLights.head, te.value * 5)
      else wheelQueue.enqueue(te)
    }
    case he: HueEvent => {
      println("Received Hue Event!")
      hue.updateLightState(he)
      if(wheelQueue.isEmpty) stateKnown = true
      else {
        val te = wheelQueue.dequeueAll(_ => true).reduce((a, b) => Wheel(a.datetime, a.source, a.value + b.value))
        stateKnown = !hue.increaseBrightnessBy(hue.allLights.head, te.value * 5)
      }
    }
    case hError: PHHueError => {
      println("Received Error Event!")
      if(wheelQueue.isEmpty) stateKnown = true
      else {
        val te = wheelQueue.dequeueAll(_ => true).reduce((a, b) => Wheel(a.datetime, a.source, a.value + b.value))
        stateKnown = !hue.increaseBrightnessBy(hue.allLights.head, te.value * 5)
      }
    }
    case other => println(s"don't know what to do with anything but a wheel :(" + other)
  }
}

object HueActor {
  val name = "HueActor"
}
