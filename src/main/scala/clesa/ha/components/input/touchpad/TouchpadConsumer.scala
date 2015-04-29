package clesa.ha.components.input.touchpad

import java.io.{FileInputStream, DataInputStream}
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import org.apache.camel.Processor
import org.apache.camel.impl.{DefaultMessage, DefaultConsumer}
import org.joda.time.DateTime

class TouchpadConsumer(endpoint: TouchpadEndpoint, processor: Processor)
  extends DefaultConsumer(endpoint, processor) {

  val eventFilePath = s"/dev/input/${endpoint.eventFileId}"
  val fileStream = new DataInputStream(new FileInputStream(eventFilePath))
  val touchEvent = new Array[Byte](24)
  val es = Executors.newFixedThreadPool(1)

  override def doStart() = {
    super.doStart()

    es.submit(new Runnable {
      override def run(): Unit = {
        while (true) {
          fileStream.readFully(touchEvent)
          val byteBuffer = ByteBuffer.wrap(touchEvent.reverse)
          val touchValue = byteBuffer.getInt
          val touchCode = byteBuffer.getShort
          val myType = byteBuffer.getShort
          val numMicroSeconds = byteBuffer.getLong
          val numSeconds = byteBuffer.getLong
          val datetime = new DateTime(numSeconds * 1000L + numMicroSeconds)
          //println(datetime)
          //println(s"myType: $myType")
          //println(s"touchCode:  $touchCode")
          //println(s"touchValue:  $touchValue")
          val e = endpoint.createExchange()
          val message = new DefaultMessage
          message.setBody(TouchpadEvent(datetime, eventFilePath, touchCode, touchValue))
          e.setIn(message)
          processor.process(e)
        }
      }
    })
  }
  override def doStop() = es.shutdown()
}
