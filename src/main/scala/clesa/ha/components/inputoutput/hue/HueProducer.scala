package clesa.ha.components.inputoutput.hue

import clesa.ha.events.{SetLightToPercent, IncreaseLightByPercent, HueEvent}
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultProducer

class HueProducer(endpoint: HueEndpoint)
  extends DefaultProducer(endpoint) {

  def myLight = endpoint.getLightByName("tv").get

  override def process(exchange: Exchange): Unit = {
    println(s"received exchange: ${exchange.getIn.getBody}")
    exchange.getIn.getBody(classOf[HueEvent]) match {
      case ilbp: IncreaseLightByPercent if ilbp.value != 0 => endpoint.increaseBrightnessByPercent(myLight, ilbp.value)
      case sltp: SetLightToPercent => endpoint.setBrightnessToPercent(myLight, sltp.value)
      case _ =>
    }

  }

  override def doStart() = {
    super.doStart()
    //do stuff
  }
}
