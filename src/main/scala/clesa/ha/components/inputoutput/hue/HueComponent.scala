package clesa.ha.components.inputoutput.hue

import org.apache.camel.Endpoint
import org.apache.camel.impl.DefaultComponent

class HueComponent
  extends DefaultComponent {
  override def createEndpoint(uri: String, remaining: String, parameters: java.util.Map[String, AnyRef]): Endpoint = {
    val ipAddress = remaining
    new HueEndpoint(uri, ipAddress)
  }
}
