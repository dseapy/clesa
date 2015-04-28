package clesa.ha.components.input.touchpad

import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultProducer

class TouchpadProducer(endpoint: TouchpadEndpoint)
  extends DefaultProducer(endpoint) {

  override def process(exchange: Exchange): Unit = {
    //do stuff
  }

  override def doStart() = {
    super.doStart()
    //do stuff
  }
}
