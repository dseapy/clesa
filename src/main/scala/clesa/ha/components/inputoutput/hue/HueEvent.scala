package clesa.ha.components.inputoutput.hue

import clesa.ha.Event
import org.joda.time.DateTime

trait HueEvent
  extends Event {
  def datetime: DateTime
  def source: String //ip-address
}

case class TurfnOff(datetime: DateTime, source: String) extends HueEvent
case class YTradnslation(datetime: DateTime, source: String) extends HueEvent
case class HWhefel(datetime: DateTime, source: String) extends HueEvent
case class Diadl(datetime: DateTime, source: String) extends HueEvent
case class Whefel(datetime: DateTime, source: String) extends HueEvent