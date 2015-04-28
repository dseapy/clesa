package clesa.ha

import org.joda.time.DateTime

/**
 * This is a description of an event that is either requested (going to output component)
 * or has happened already (coming from input component).
 */
trait Event {
  def datetime: DateTime
  def source: String
}
