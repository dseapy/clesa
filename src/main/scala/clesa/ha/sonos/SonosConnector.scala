package clesa.ha.sonos

import clesa.ha.sonos.zone.Zone
import clesa.ha.utils.RestUtils._
import com.typesafe.config.{ConfigRenderOptions, ConfigFactory}

import collection.JavaConversions._

case class SonosConnector(ip: String) {
  val baseUrl = s"http://$ip:5005"

  def zones: Seq[Zone] = {
    val zoneConfig = ConfigFactory.parseString("zones: " + get(s"$baseUrl/zones"))
    println(zoneConfig.root().render(ConfigRenderOptions.defaults().setJson(false).setComments(false).setOriginComments(false)))
    zoneConfig.getConfigList("zones").map(c => Zone(c))
  }

  def zone(roomName: String) = get(s"$baseUrl/$roomName/state")

  /* Apply to all ZONES */

  def lockAllVolumes = get(s"$baseUrl/lockvolumes")
  def unlockAllVolumes = get(s"$baseUrl/unlockvolumes")
  def sleepAll(seconds: Int) = get(s"$baseUrl/sleep/$seconds")
  def reindexAll = get(s"$baseUrl/reindex")

  //timeout is the number of minutes before the command takes affect
  def pauseAll(timeoutInMinutes: Option[Int] = None) =
    get(s"$baseUrl/pauseAll") + timeoutInMinutes.map(timeout => s"/$timeout").getOrElse("")

  //timeout is the number of minutes before the command takes affect
  def resumeAll(timeoutInMinutes: Option[Int] = None) =
    get(s"$baseUrl/resumeall") + timeoutInMinutes.map(timeout => s"/$timeout").getOrElse("")

  def sayAll(roomName: String, text: String) = get(s"$baseUrl/$roomName/sayall/$text")

  /* Specific to a ZONE */

  def setVolume(roomName: String, volume: Int) = get(s"$baseUrl/$roomName/volume/$volume")

  def increaseVolumeBy(roomName: String, volume: Int) = {
    if(volume > 0)
      get(s"$baseUrl/$roomName/volume/+$volume")
    else if(volume < 0 )
      get(s"$baseUrl/$roomName/volume/$volume")
  }

  def next(roomName: String) = get(s"$baseUrl/$roomName/next")
  def pause(roomName: String) = get(s"$baseUrl/$roomName/pause")
  def play(roomName: String) = get(s"$baseUrl/$roomName/play")
  def mute(roomName: String) = get(s"$baseUrl/$roomName/mute")
  def unmute(roomName: String) = get(s"$baseUrl/$roomName/unmute")
  def seek(roomName: String, queueIndex: Int) = get(s"$baseUrl/$roomName/seek/$queueIndex")
  def trackSeek(roomName: String, seconds: Int) = get(s"$baseUrl/$roomName/trackseek/$seconds")
  def previous(roomName: String) = get(s"$baseUrl/$roomName/previous")

  //Toggles playing state
  def playpause(roomName: String) = get(s"$baseUrl/$roomName/playpause")
  
  def repeatOn(roomName: String) = get(s"$baseUrl/$roomName/repeat/on")
  def repeatOff(roomName: String) = get(s"$baseUrl/$roomName/repeat/off")
  def shuffleOn(roomName: String) = get(s"$baseUrl/$roomName/shuffle/on")
  def shuffleOff(roomName: String) = get(s"$baseUrl/$roomName/shuffle/off")
  def crossfadeOn(roomName: String) = get(s"$baseUrl/$roomName/crossfade/on")
  def crossfadeOff(roomName: String) = get(s"$baseUrl/$roomName/crossfade/off")
  def clearQueue(roomName: String) = get(s"$baseUrl/$roomName/clearqueue")
  def playFavorite(roomName: String, playlist: String) = get(s"$baseUrl/$roomName/favorite/$playlist")
  def playPlaylist(roomName: String, playlist: String) = get(s"$baseUrl/$roomName/playlist/$playlist")
  def say(roomName: String, text: String) = get(s"$baseUrl/$roomName/say/$text")
}
