package clesa.ha.sonos.zone

import com.typesafe.config.Config
import scala.collection.JavaConversions._

case class Zone(id: String, coordinator: Member, members: Seq[Member])

case object Zone{
  def apply(zoneConfig: Config): Zone = {
    val coordinator = Member(zoneConfig.getConfig("coordinator"))
    val members = zoneConfig.getConfigList("members").map(c => Member(c))
    Zone(zoneConfig.getString("uuid"), coordinator, members)
  }
}