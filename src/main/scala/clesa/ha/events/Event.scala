package clesa.ha.events

import org.joda.time.DateTime

/**
 * An event that has happened.
 * Has a datetime when it happened, and a source that identifies specifically what device (ie. touchpad event3) it happened to
 */
trait Event {
  def datetime: DateTime
  def source: String
}
