package clesa.ha.components.input.touchpad

import java.io.{FileInputStream, DataInputStream}
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
          val datetime = new DateTime(fileStream.readLong())
          val touchCode = fileStream.readShort()
          val touchValue = fileStream.readInt()
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
