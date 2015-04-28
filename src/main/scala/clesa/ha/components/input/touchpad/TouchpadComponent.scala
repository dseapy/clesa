package clesa.ha.components.input.touchpad

import org.apache.camel.Endpoint
import org.apache.camel.impl.DefaultComponent

/**
 * This component takes the input from a touchpad with the provided event id in "remaining".
 * It produces a TouchpadEvent.
 * ie. from(touchpad:event13).to(direct:handleTouchpadEvent)
 */
class TouchpadComponent
  extends DefaultComponent {
  override def createEndpoint(uri: String, remaining: String, parameters: java.util.Map[String, AnyRef]): Endpoint = {
    val eventFileId = remaining
    new TouchpadEndpoint(eventFileId, uri)
  }
}
