package clesa.ha.events.linuxinput

import clesa.ha.events.Event
import com.typesafe.scalalogging.slf4j.Logging
import org.joda.time.DateTime

trait LinuxInputEvent
  extends Event {
  def datetime: DateTime
  def source: String
  def code: Int
  def value: Int
}
object LinuxInputEvent extends Logging {
  def apply(datetime: DateTime, source: String, code: Short, value: Int): Option[LinuxInputEvent] = {
    logger.trace(s"$source $code $value")
    code match {
      case XTranslation.code =>     Some(XTranslation(datetime, source, value))
      case YTranslation.code =>     Some(YTranslation(datetime, source, value))
      case HWheel.code =>           Some(HWheel(datetime, source, value))
      case Dial.code =>             Some(Dial(datetime, source, value))
      case VWheel.code =>           Some(VWheel(datetime, source, value))
      case RightButtonClick.code => Some(RightButtonClick(datetime, source, value))
      case LeftButtonClick.code =>  Some(LeftButtonClick(datetime, source, value))
      case FromLeftSwipe.code =>    Some(FromLeftSwipe(datetime, source, value))
      case FromRightSwipe.code =>   Some(FromRightSwipe(datetime, source, value))
      case Misc.code =>             Some(Misc(datetime, source, value))
      case other =>
        logger.error(s"Could not create touchpad event for code $other")
        None
    }
  }
}

trait ButtonClick extends LinuxInputEvent
trait SwipeFromSide extends LinuxInputEvent {
  def fromRight: Boolean
}
trait Translation extends LinuxInputEvent
trait Wheel extends LinuxInputEvent

case class Misc(datetime: DateTime, source: String, value: Int) extends LinuxInputEvent{val code = Misc.code}
object Misc{ val code = 4 }

case class XTranslation(datetime: DateTime, source: String, value: Int) extends Translation{val code = XTranslation.code}
object XTranslation{ val code = 0 }

case class YTranslation(datetime: DateTime, source: String, value: Int) extends Translation{val code = YTranslation.code}
object YTranslation{ val code = 1 }

case class LeftButtonClick(datetime: DateTime, source: String, value: Int) extends ButtonClick{val code = LeftButtonClick.code}
object LeftButtonClick{ val code = 272 }

case class RightButtonClick(datetime: DateTime, source: String, value: Int) extends ButtonClick{val code = RightButtonClick.code}
object RightButtonClick{ val code = 273 }

case class FromLeftSwipe(datetime: DateTime, source: String, value: Int) extends SwipeFromSide{val code = FromLeftSwipe.code; val fromRight = FromLeftSwipe.fromRight}
object FromLeftSwipe{ val code = 14; val fromRight = false }

case class FromRightSwipe(datetime: DateTime, source: String, value: Int) extends SwipeFromSide{val code = FromRightSwipe.code; val fromRight = FromRightSwipe.fromRight}
object FromRightSwipe{ val code = 193; val fromRight = true }

case class HWheel(datetime: DateTime, source: String, value: Int) extends Wheel{val code = HWheel.code}
object HWheel{ val code = 6 }

case class Dial(datetime: DateTime, source: String, value: Int) extends LinuxInputEvent{val code = Dial.code}
object Dial{ val code = 7 }

case class VWheel(datetime: DateTime, source: String, value: Int) extends Wheel{val code = VWheel.code}
object VWheel{ val code = 8 }