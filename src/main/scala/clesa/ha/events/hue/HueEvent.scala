package clesa.ha.events.hue

import clesa.ha.events.Event
import org.joda.time.DateTime

trait HueEvent
  extends Event {
  def datetime: DateTime
  def source: String //ip-address
  def code: Int
  def isOfSameHueEventType(other: HueEvent) = other.code == this.code
  def combineWith(other: HueEvent) = this
}

case class IncreaseLightByPercent(datetime: DateTime, source: String, value: Int) extends HueEvent{
  val code = IncreaseLightByPercent.code
  override def combineWith(other: HueEvent) = other match {
    case ilbp: IncreaseLightByPercent => this.copy(value = this.value + ilbp.value)
    case _ => super.combineWith(other)
  }
}
case object IncreaseLightByPercent{ val code: Int = 0 }
case class SetLightToPercent(datetime: DateTime, source: String, value: Int) extends HueEvent{ val code = SetLightToPercent.code }
case object SetLightToPercent{ val code: Int = 1 }