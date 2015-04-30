package clesa.ha.controllers

import clesa.ha.Event
import clesa.ha.events.{Wheel, IncreaseLightByPercent}
import org.apache.camel.{Processor, Exchange}

class HueController extends Processor {
  def process(exchange: Exchange): Unit = {
    println(exchange.getIn.getBody)
    println(exchange.getIn.getBody(classOf[Event]))
    exchange.getIn.getBody(classOf[Event]) match {
      case te: Wheel => exchange.getIn.setBody(IncreaseLightByPercent(te.datetime, te.source, te.value * 5))
                        exchange.getIn.setHeader("toHue", true)
      case _ =>
    }
  }
}