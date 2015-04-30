package clesa.ha.events.linuxinput

import clesa.ha.events.Event
import org.apache.log4j.Logger
import org.joda.time.DateTime

trait LinuxInputEvent
  extends Event {
  def datetime: DateTime
  def source: String
  def code: Int
  def value: Int
}
object LinuxInputEvent{
  def apply(datetime: DateTime, source: String, code: Short, value: Int): Option[LinuxInputEvent] = {
    code match {
      case XTranslation.code => Some(XTranslation(datetime, source, value))
      case YTranslation.code => Some(YTranslation(datetime, source, value))
      case HWheel.code => Some(HWheel(datetime, source, value))
      case Dial.code => Some(Dial(datetime, source, value))
      case Wheel.code => Some(Wheel(datetime, source, value))
      case other => Logger.getLogger(LinuxInputEvent.getClass).error(s"Could not create touchpad event for code $other")
                    None
    }
  }
}

case class XTranslation(datetime: DateTime, source: String, value: Int) extends LinuxInputEvent{val code = XTranslation.code}
object XTranslation{ val code = 0 }

case class YTranslation(datetime: DateTime, source: String, value: Int) extends LinuxInputEvent{val code = YTranslation.code}
object YTranslation{ val code = 1 }

case class HWheel(datetime: DateTime, source: String, value: Int) extends LinuxInputEvent{val code = HWheel.code}
object HWheel{ val code = 6 }

case class Dial(datetime: DateTime, source: String, value: Int) extends LinuxInputEvent{val code = Dial.code}
object Dial{ val code = 7 }

case class Wheel(datetime: DateTime, source: String, value: Int) extends LinuxInputEvent{val code = Wheel.code}
object Wheel{ val code = 8 }