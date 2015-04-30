package clesa.ha

import clesa.ha.aggregators.HueEventAggregationStrategy
import clesa.ha.components.input.touchpad.TouchpadComponent
import clesa.ha.components.inputoutput.hue.HueComponent
import clesa.ha.controllers.HueController
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import org.apache.log4j.Logger

object ClesaApp
  extends App {
  //val configPath: String = "/opt/clesa/primary.conf"
  Logger.getLogger(ClesaApp.getClass).debug("Hello")
  val context: CamelContext = new DefaultCamelContext
  context.addComponent("hue", new HueComponent)
  context.addComponent("touchpad", new TouchpadComponent)
  context.setTracing(true)
  val hueController = new HueController()
  val routeBuilder = new ScalaRouteBuilder(context) {
    "touchpad:event3" --> "direct:hueController"
    from("direct:hueController").filter(simple("${in.header.foo}")).to("direct:toHue")
    "direct:toHue" process hueController aggregate(constant("id"), new HueEventAggregationStrategy) completionInterval(HueEventAggregationStrategy.completionInterval) to "hue:192.168.0.152"
  }
  context.addRoutes(routeBuilder)
  context.start()
  //while(true) {Thread.sleep(300000L)} //sleep for 30 seconds
}
