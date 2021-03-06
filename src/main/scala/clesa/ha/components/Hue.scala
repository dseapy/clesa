package clesa.ha.components

import java.util

import clesa.ha.events.hue.HueEvent
import com.philips.lighting.hue.listener.PHLightListener
import com.philips.lighting.hue.sdk.{PHAccessPoint, PHHueSDK}
import com.philips.lighting.model.{PHLightState, PHBridgeResource, PHHueError, PHLight}
import com.typesafe.scalalogging.slf4j.Logging

import scala.collection.JavaConversions._

class Hue(eventCallbackFunc: HueEvent => Unit,
          errorCallbackFunc: PHHueError => Unit,
          ipAddress: String,
          appName: String = Hue.defaultAppName,
          deviceName: String = Hue.defaultDeviceName,
          username: String = Hue.defaultUserName) extends Logging {

  val hueSdk = {
    val hSdk = PHHueSDK.create()
    hSdk.setAppName(appName)
    hSdk.setDeviceName(deviceName)
    hSdk
  }

  val accessPoint = {
    val ap = new PHAccessPoint()
    ap.setIpAddress(ipAddress)
    ap.setUsername(username)
    ap
  }

  blockUntilConnected()

  val bridge = hueSdk.getSelectedBridge
  hueSdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL)
  val bridgeCache = bridge.getResourceCache
  val allLights = bridgeCache.getAllLights
  val pHLightListener = new PHLightListener {
    def onReceivingLights(list: util.List[PHBridgeResource]): Unit = {}
    def onSearchComplete(): Unit = {}
    def onReceivingLightDetails(phLight: PHLight): Unit = {}
    def onError(i: Int, s: String): Unit = errorCallbackFunc(new PHHueError(i, s, ipAddress))
    def onStateUpdate(updatedStateMap: util.Map[String, String], list: util.List[PHHueError]): Unit = {
      HueEvent(updatedStateMap).foreach(eventCallbackFunc)
    }
    def onSuccess(): Unit = {}
  }

  def blockUntilConnected(): Unit = {
    if(!hueSdk.isAccessPointConnected(accessPoint)){
      hueSdk.connect(accessPoint)
      while(!hueSdk.isAccessPointConnected(accessPoint)){
        logger.info("Waiting to connect to hue...")
        Thread.sleep(300L)
      }
      logger.info("Connected to hue!")
    }
  }
  def updateLightState(hueEvent: HueEvent): Unit =
    for(light <- allLights.find(_.getIdentifier == hueEvent.source)) {
      light.setLastKnownLightState(hueEvent.updateState(light.getLastKnownLightState))
    }
  def increaseHueBy(light: PHLight, inc: Int): Boolean =
    setHueTo(light, light.getLastKnownLightState.getHue + inc)
  def setHueTo(light: PHLight, hue: Int): Boolean = {
    if(light.getLastKnownLightState.isOn){//don't mess with hue if light is off
      val updatedHue = if(hue < Hue.minHue) Hue.maxHue + 1 - (-hue % Hue.maxHue) else (hue % (Hue.maxHue + 1)) + Hue.minHue
      logger.info(s"updatedHue: $updatedHue")
      sendNewLightState(light, onOption = Some(true), hueOption = Some(updatedHue))
    } else false
  }
  def increaseSaturationBy(light: PHLight, inc: Int): Boolean =
    setSaturationTo(light, light.getLastKnownLightState.getSaturation + inc)
  def setSaturationTo(light: PHLight, bri: Int): Boolean = {
    if(light.getLastKnownLightState.isOn) { //don't mess with saturation if light is off
      val updatedSaturation = bri min Hue.maxSaturation max Hue.minSaturation
      sendNewLightState(light, onOption = Some(true), satOption = Some(updatedSaturation))
    } else false
  }
  def increaseBrightnessBy(light: PHLight, inc: Int): Boolean =
    setBrightnessTo(light, light.getLastKnownLightState.getBrightness + inc)
  def setBrightnessTo(light: PHLight, bri: Int): Boolean = {
    val updatedBrightness = bri min Hue.maxBrightness
    if(updatedBrightness < Hue.minBrightness) setOff(light)
    else sendNewLightState(light, onOption = Some(true), briOption = Some(updatedBrightness))
  }
  def setOff(light: PHLight): Boolean = sendNewLightState(light, onOption = Some(false))
  def toggle(light: PHLight): Boolean = {
    val isOn = light.getLastKnownLightState.isOn
    val briOption = if(!isOn) Some(Hue.maxBrightness) else None
    sendNewLightState(light, Some(!isOn), briOption)
  }
  def sendNewLightState(light: PHLight,
                        onOption: Option[Boolean] = None,
                        briOption: Option[Int] = None,
                        satOption: Option[Int] = None,
                        hueOption: Option[Int] = None ): Boolean = {
    val updatedLightState = new PHLightState
    onOption.find(_ != light.getLastKnownLightState.isOn).foreach(on => updatedLightState.setOn(on))
    briOption.find(_ != light.getLastKnownLightState.getBrightness || (!light.getLastKnownLightState.isOn && onOption.exists(_ == true))).foreach(bri => updatedLightState.setBrightness(bri))
    satOption.find(_ != light.getLastKnownLightState.getSaturation).foreach(sat => updatedLightState.setSaturation(sat))
    hueOption.find(_ != light.getLastKnownLightState.getHue).foreach(hue => updatedLightState.setHue(hue))
    if(updatedLightState != new PHLightState){
      updatedLightState.setTransitionTime(Hue.myDefaultTransitionTime)
      bridge.updateLightState(light, updatedLightState, pHLightListener)
      true
    } else false

  }
  def findLightByNames(names: Seq[String]): Option[PHLight] = findLightsByNames(names).headOption
  def findLightsByNames(names: Seq[String]): Seq[PHLight] = names.flatMap { name => allLights.find(_.getName.equalsIgnoreCase(name)) }
  def getLightById(id: String): Option[PHLight] = allLights.find(_.getIdentifier.equalsIgnoreCase(id))
  def getLightByName(name: String): Option[PHLight] = allLights.find(_.getName.equalsIgnoreCase(name))
  def lightStates = allLights.map(l => l -> l.getLastKnownLightState).toMap
}

object Hue{
  val defaultAppName = "clesa"
  val defaultDeviceName = "ha"
  val defaultUserName = "newdeveloper"
  val minSaturation = 0
  val minBrightness = 0
  val maxSaturation = 254
  val maxBrightness = 254
  val minHue = 0
  val maxHue = 65534
  val myDefaultTransitionTime = 1
}
