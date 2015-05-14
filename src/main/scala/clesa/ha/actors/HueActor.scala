package clesa.ha.actors

import akka.actor.{ActorRef, Actor}
import clesa.ha.components.Hue
import clesa.ha.events.hue.HueEvent
import clesa.ha.events.linuxinput._
import com.philips.lighting.model.{PHLight, PHHueError}
import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.Logging
import org.joda.time.DateTime
import collection.JavaConversions._

class HueActor(broadcastActor: ActorRef,
               ipAddress: String,
               inputSourceId: String,
               lightId1: String,
               lightId2: String)
  extends Actor with Logging {

  def this(bActor: ActorRef, config: Config) =
    this(bActor,
         config.getString("ip-address"),
         config.getString("input-source-id"),
         config.getString("light-id-1"),
         config.getString("light-id-2"))

  var stateKnown = true
  var lastButtonClickTime = new DateTime(0L)
  var lastSwipeFromSideTime = new DateTime(0L)
  var lastWheelTime = new DateTime(0L)

  //useful for button presses and other non-cumulative actions
  def enoughTimePassedSinceLastEvent(oldTime: DateTime, newTime: DateTime) = oldTime plusMillis 300 isBefore newTime

  val hue = new Hue(hueEvent => broadcastActor ! hueEvent,
                    hueError => self ! hueError,
                    ipAddress)

  val sourceToLightIdsMap = Map(inputSourceId -> LightIdsWithActive(lightId1, lightId2,activeLightFirst = true))
  case class LightIdsWithActive(lightId0: String, lightId1: String, var activeLightFirst: Boolean){
    def getActiveLightId = if(activeLightFirst) lightId0 else lightId1
    def getActiveLightFromSource(source: String) = hue.allLights.find(_.getIdentifier == getActiveLightId)
  }
  def getActiveLightOptionFromSource(source: String): Option[PHLight] =
    sourceToLightIdsMap.get(source).flatMap(_.getActiveLightFromSource(source))

  def receive = {
    case yt: YTranslation =>
      if(enoughTimePassedSinceLastEvent(lastWheelTime, yt.datetime)) {
        if (stateKnown) stateKnown =
          getActiveLightOptionFromSource(yt.source).filter(_.supportsColor && yt.value != 0).map { light =>
            !hue.increaseSaturationBy(light, -yt.value * HueActor.satSensitivity)
          }.getOrElse(true)
      }
    case xt: XTranslation =>
      if(enoughTimePassedSinceLastEvent(lastWheelTime, xt.datetime)) {
        if (stateKnown) stateKnown =
          getActiveLightOptionFromSource(xt.source).filter(_.supportsColor && xt.value != 0).map { light =>
            !hue.increaseHueBy(light, xt.value * HueActor.hueSensitivity)
          }.getOrElse(true)
      }
    case te: VWheel => {
      lastWheelTime = te.datetime
      if(stateKnown) stateKnown =
        getActiveLightOptionFromSource(te.source).map{ light =>
          !hue.increaseBrightnessBy(light, te.value * HueActor.briSensitivity)
        }.getOrElse(true)
    }
    case bc: ButtonClick =>
      val enoughTimePassed = enoughTimePassedSinceLastEvent(lastButtonClickTime, bc.datetime)
      lastButtonClickTime = bc.datetime
      if(stateKnown && enoughTimePassed)
        stateKnown = getActiveLightOptionFromSource(bc.source).map{ light => !hue.toggle(light) }.getOrElse(true)
    case rs: SwipeFromSide =>
      val enoughTimePassed = enoughTimePassedSinceLastEvent(lastSwipeFromSideTime, rs.datetime)
      lastSwipeFromSideTime = rs.datetime
      if(enoughTimePassed) {
        sourceToLightIdsMap.get(rs.source).foreach(_.activeLightFirst = rs.fromRight)
      }
    case he: HueEvent => hue.updateLightState(he)
                         stateKnown = true
    case hError: PHHueError =>
      logger.error(hError.getMessage)
      stateKnown = true
    case other =>
  }
}

object HueActor {
  val name = "HueActor"
  val hueSensitivity = 20
  val satSensitivity = 5
  val briSensitivity = 5
}
