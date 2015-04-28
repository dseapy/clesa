package clesa.ha.components.output.sonos.zone

import com.typesafe.config.Config

case class Member(zoneId: String,
                  groupState: GroupState,
                  playMode: PlayMode,
                  roomName: String,
                  state: MemberState)

case object Member {
  def apply(memberConfig: Config): Member = {
    val zoneId = memberConfig.getString("coordinator")
    val groupState = GroupState(memberConfig.getConfig("groupState"))
    val playMode = PlayMode(memberConfig.getConfig("playMode"))
    val roomName = memberConfig.getString("roomName")
    val state = MemberState(memberConfig.getConfig("state"))
    Member(zoneId, groupState, playMode, roomName, state)
  }
}

case class GroupState(mute: Boolean, volume: Int)
case object GroupState {
  def apply(gsConfig: Config): GroupState = GroupState(gsConfig.getBoolean("mute"), gsConfig.getInt("volume"))
}

case class PlayMode(crossfade: Boolean, repeat: Boolean, shuffle: Boolean)
case object PlayMode {
 def apply(pmConfig: Config): PlayMode = PlayMode(pmConfig.getBoolean("crossfade"),
                                                  pmConfig.getBoolean("repeat"),
                                                  pmConfig.getBoolean("shuffle"))
}
case class MemberState(currentTrack: CurrentTrack,
                       elapsedTime: Int,
                       elapsedTimeFormatted: String,
                       mute: Boolean,
                       nextTrack: NextTrack,
                       playerState: String,
                       trackNo: Int,
                       volume: Int,
                       playMode: PlayMode,
                       zoneState: String)
case object MemberState {
  def apply(msConfig: Config): MemberState = {

    val currentTrack = CurrentTrack(msConfig.getConfig("currentTrack"))
    val elapsedTime = msConfig.getInt("elapsedTime")
    val elapsedTimeFormatted = msConfig.getString("elapsedTimeFormatted")
    val mute = msConfig.getBoolean("mute")
    val nextTrack = NextTrack(msConfig.getConfig("nextTrack"))
    val playerState = msConfig.getString("playerState")
    val trackNo = msConfig.getInt("trackNo")
    val volume = msConfig.getInt("volume")
    val zonePlayMode = PlayMode(msConfig.getConfig("zonePlayMode"))
    val zoneState = msConfig.getString("zoneState")
    MemberState(currentTrack,
                elapsedTime,
                elapsedTimeFormatted,
                mute,
                nextTrack,
                playerState,
                trackNo,
                volume,
                zonePlayMode,
                zoneState)
  }
}
case class CurrentTrack(album: String,
                        albumArtURI: String,
                        artist: String,
                        duration: Int,
                        radioShowMetaData: String,
                        streamInfo: String,
                        title: String,
                        ctype: String,
                        uri: String)
case object CurrentTrack {
  def apply(ctConfig: Config): CurrentTrack = {
    val album = ctConfig.getString("album")
    val albumArtURI = ctConfig.getString("albumArtURI")
    val artist = ctConfig.getString("artist")
    val duration = ctConfig.getInt("duration")
    val radioShowMetaData = ctConfig.getString("radioShowMetaData")
    val streamInfo = ctConfig.getString("streamInfo")
    val title = ctConfig.getString("title")
    val ctype = ctConfig.getString("type")
    val uri = ctConfig.getString("uri")
    CurrentTrack(album, albumArtURI, artist, duration, radioShowMetaData, streamInfo, title, ctype, uri)
  }
}
case class NextTrack(album: String,
                     albumArtURI: String,
                     artist: String,
                     title: String,
                     uri: String)
case object NextTrack {
  def apply(ntConfig: Config): NextTrack = {
    val album = ntConfig.getString("album")
    val albumArtURI = ntConfig.getString("albumArtURI")
    val artist = ntConfig.getString("artist")
    val title = ntConfig.getString("title")
    val uri = ntConfig.getString("uri")
    NextTrack(album, albumArtURI, artist, title, uri)
  }
}