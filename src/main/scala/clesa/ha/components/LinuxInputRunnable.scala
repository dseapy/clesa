package clesa.ha.components

import java.io.{FileInputStream, DataInputStream}
import java.nio.ByteBuffer

import clesa.ha.events.linuxinput.LinuxInputEvent
import org.joda.time.DateTime

/**
 * xinput --list
 * xinput --list-props 9
 * export DISPLAY=:0
 * xinput set-int-prop 9 "Device Enabled" 8 0
 *
 * @param eventFilePath Path to the linux input event file (ie. "/dev/input/event3")
 * @param callbackFunction What to do when there is an event (ie. otherActor ! result)
 */
class LinuxInputRunnable(eventFilePath: String, callbackFunction: LinuxInputEvent => Unit)
  extends Runnable {

  val fileStream = new DataInputStream(new FileInputStream(eventFilePath))
  val inputEvent = new Array[Byte](24)

  override def run(): Unit = {
    while (true) {
      fileStream.readFully(inputEvent)
      val byteBuffer = ByteBuffer.wrap(inputEvent.reverse)
      val touchValue = byteBuffer.getInt
      val touchCode = byteBuffer.getShort
      val myType = byteBuffer.getShort
      val numMicroSeconds = byteBuffer.getLong
      val numSeconds = byteBuffer.getLong
      LinuxInputEvent(new DateTime(), eventFilePath, touchCode, touchValue).foreach(callbackFunction)
    }
  }
}
