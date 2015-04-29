package clesa.ha.components.inputoutput.hue

import clesa.ha.components.input.touchpad.{Wheel, TouchpadEvent}
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultProducer

class HueProducer(endpoint: HueEndpoint)
  extends DefaultProducer(endpoint) {

  def myLight = endpoint.getLightByName("tv").get

  override def process(exchange: Exchange): Unit = {
    exchange.getIn.getBody(classOf[TouchpadEvent]) match{
      case wheel: Wheel => {
        println(wheel)
        println(wheel.value)
        println(endpoint.allLights)
        println(myLight)
        if(wheel.value != 0) endpoint.increaseBrightnessByPercent(myLight, wheel.value * 5)
      }
      case _ =>
    }
    //do stuff
  }

  override def doStart() = {
    super.doStart()
    //do stuff
  }
}
