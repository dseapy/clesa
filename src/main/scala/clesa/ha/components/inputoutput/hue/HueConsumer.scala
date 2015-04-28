package clesa.ha.components.inputoutput.hue

import org.apache.camel.Processor
import org.apache.camel.impl.DefaultConsumer

class HueConsumer(endpoint: HueEndpoint, processor: Processor)
  extends DefaultConsumer(endpoint, processor)
