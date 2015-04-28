package clesa.ha.components.inputoutput.hue

import clesa.ha.Event
import org.joda.time.DateTime

trait HueEvent
  extends Event {
  def datetime: DateTime
  def source: String //ip-address
}

case class TurnOff(datetime: DateTime, source: String) extends HueEvent
case class YTranslation(datetime: DateTime, source: String) extends HueEvent
case class HWheel(datetime: DateTime, source: String) extends HueEvent
case class Dial(datetime: DateTime, source: String) extends HueEvent
case class Wheel(datetime: DateTime, source: String) extends HueEvent