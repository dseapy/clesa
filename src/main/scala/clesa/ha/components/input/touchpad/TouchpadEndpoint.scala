package clesa.ha.components.input.touchpad

import org.apache.camel.impl.DefaultPollingEndpoint
import org.apache.camel.{Producer, Processor}

case class TouchpadEndpoint(eventFileId: String, uri: String)
  extends DefaultPollingEndpoint {
  setEndpointUri(uri)
  override def isSingleton: Boolean = true
  override def createConsumer(processor: Processor) = new TouchpadConsumer(this, processor)
  override def createProducer(): Producer = new TouchpadProducer(this)
}
