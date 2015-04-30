package clesa.ha.events.hue

import clesa.ha.events.Event
import com.philips.lighting.model.PHLightState
import org.joda.time.DateTime
import scala.util.Try
import collection.JavaConversions._

case class HueEvent(datetime: DateTime,
                    source: String,
                    updateMap: java.util.Map[String, String])
  extends Event {
  def updateState(id: String, phls: PHLightState): PHLightState = {
    val retLightState = new PHLightState(phls)
    Try {
      if (id == source) {
        for((prop, value) <- updateMap){
          prop match {
            case "bri" => retLightState.setBrightness(value.toInt)
            case "on" => retLightState.setOn(value.toBoolean)
          }
        }
      }
      retLightState
    }.getOrElse(phls)
  }
}

object HueEvent {
  //     /lights/2/state/bri=0, /lights/2/state/on=true
  def apply(updateMap: java.util.Map[String, String]): Option[HueEvent] = {
    Try{
      val source = updateMap.head._1.split("/")(2)
      val myMap = for((prop, value) <- updateMap) yield (prop.split("/")(4), value)
      HueEvent(new DateTime(), source, myMap)
    }.toOption
  }
}