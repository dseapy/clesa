package clesa.ha

import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import org.apache.log4j.Logger
import org.apache.log4j.Level

object ClesaApp
  extends App {
  //val configPath: String = "/opt/clesa/primary.conf"
  Logger.getRootLogger.setLevel(Level.DEBUG)
  val context: CamelContext = new DefaultCamelContext
  context.setTracing(true)
  val routeBuilder = new ScalaRouteBuilder(context) {
    "touchpad:event3" --> "hue:192.168.0.152"
  }
  context.addRoutes(routeBuilder)
  context.start()
  Thread.sleep(30000L) //sleep for 30 seconds
}
