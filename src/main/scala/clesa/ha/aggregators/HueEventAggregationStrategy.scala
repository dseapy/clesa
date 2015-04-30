package clesa.ha.aggregators

import clesa.ha.events.HueEvent
import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AggregationStrategy

class HueEventAggregationStrategy
  extends AggregationStrategy {
  def aggregate(oldExchange: Exchange, newExchange: Exchange): Exchange = {
    // the first time we only have the new exchange so it wins the first round
    if(oldExchange == null) return newExchange
    println(s"Hue Aggregation Strategy: Aggregating $oldExchange, $newExchange")
    val newHueEvent = newExchange.getIn.getBody(classOf[HueEvent])
    val oldHueEvent = oldExchange.getIn.getBody(classOf[HueEvent])
    oldExchange.getIn.setBody(oldHueEvent combineWith newHueEvent)
    oldExchange
  }
}

object HueEventAggregationStrategy {
  val completionInterval = 150L // .05 seconds to aggregate
}
