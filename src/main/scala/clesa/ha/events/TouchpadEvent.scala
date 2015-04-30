package clesa.ha.events

import clesa.ha.Event
import org.joda.time.DateTime

trait TouchpadEvent
  extends Event {
  def datetime: DateTime
  def source: String
  def code: Int
  def value: Int
}
object TouchpadEvent{
  def apply(datetime: DateTime, source: String, code: Short, value: Int): TouchpadEvent = {
    code match {
      case XTranslation.code => XTranslation(datetime, source, value)
      case YTranslation.code => YTranslation(datetime, source, value)
      case HWheel.code => HWheel(datetime, source, value)
      case Dial.code => Dial(datetime, source, value)
      case Wheel.code => Wheel(datetime, source, value)
      case _ => throw new Exception("Problem with touch code not being found.")
    }
  }
}

case class XTranslation(datetime: DateTime, source: String, value: Int) extends TouchpadEvent{val code = XTranslation.code}
object XTranslation{ val code = 0 }

case class YTranslation(datetime: DateTime, source: String, value: Int) extends TouchpadEvent{val code = YTranslation.code}
object YTranslation{ val code = 1 }

case class HWheel(datetime: DateTime, source: String, value: Int) extends TouchpadEvent{val code = HWheel.code}
object HWheel{ val code = 6 }

case class Dial(datetime: DateTime, source: String, value: Int) extends TouchpadEvent{val code = Dial.code}
object Dial{ val code = 7 }

case class Wheel(datetime: DateTime, source: String, value: Int) extends TouchpadEvent{val code = Wheel.code}
object Wheel{ val code = 8 }