package clesa.ha.components.inputoutput.hue

import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultProducer

class HueProducer(endpoint: HueEndpoint)
  extends DefaultProducer(endpoint) {

     override def process(exchange: Exchange): Unit = {
       //do stuff
     }

     override def doStart() = {
       super.doStart()
       //do stuff
     }
   }
