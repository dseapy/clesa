package clesa.ha.components

import java.util

import clesa.ha.events.hue.HueEvent
import com.philips.lighting.hue.listener.PHLightListener
import com.philips.lighting.hue.sdk.{PHAccessPoint, PHHueSDK}
import com.philips.lighting.model.{PHLightState, PHBridgeResource, PHHueError, PHLight}

import scala.collection.JavaConversions._

class Hue(eventCallbackFunc: HueEvent => Unit,
          errorCallbackFunc: PHHueError => Unit,
          ipAddress: String,
          appName: String = "clesa",
          deviceName: String = "ha",
          username: String = "newdeveloper") {

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
    def onStateUpdate(updatedStateMap: util.Map[String, String], list: util.List[PHHueError]): Unit =
      HueEvent(updatedStateMap).foreach(eventCallbackFunc)
    def onSuccess(): Unit = {}
  }

  def blockUntilConnected(): Unit = {
    if(!hueSdk.isAccessPointConnected(accessPoint)){
      hueSdk.connect(accessPoint)
      while(!hueSdk.isAccessPointConnected(accessPoint)){
        println("Waiting to connect to hue...")
        Thread.sleep(300L)
      }
      println("Connected to hue!")
    }
  }
  def updateLightState(hueEvent: HueEvent): Unit =
    allLights.foreach(l => l.setLastKnownLightState(hueEvent.updateState(l.getIdentifier, l.getLastKnownLightState)))
  def increaseBrightnessBy(light: PHLight, inc: Int): Boolean =
    setBrightnessTo(light, light.getLastKnownLightState.getBrightness + inc)
  def setBrightnessTo(light: PHLight, bri: Int): Boolean = {
    val updatedBrightness = bri min 254
    val currentBrightness = light.getLastKnownLightState.getBrightness
    if(updatedBrightness < 0) setOff(light)
    else {
      if(currentBrightness == updatedBrightness) setOn(light)
      else sendNewLightState(light, onOption = Some(true), briOption = Some(updatedBrightness))
    }
  }
  def setOff(light: PHLight): Boolean = sendNewLightState(light, onOption = Some(false))
  def setOn(light: PHLight): Boolean = sendNewLightState(light, onOption = Some(true))
  def sendNewLightState(light: PHLight,
                        onOption: Option[Boolean] = None,
                        briOption: Option[Int] = None): Boolean = {
    val updatedLightState = new PHLightState
    onOption.find(_ != light.getLastKnownLightState.isOn).foreach(on => updatedLightState.setOn(on))
    briOption.find(_ != light.getLastKnownLightState.getBrightness).foreach(bri => updatedLightState.setBrightness(bri))
    if(updatedLightState != new PHLightState){
      println(s"Submitting light state: $updatedLightState to light $light")
      bridge.updateLightState(light, updatedLightState, pHLightListener)
      true
    } else false

  }
  def findLightByNames(names: Seq[String]): Option[PHLight] = findLightsByNames(names).headOption
  def findLightsByNames(names: Seq[String]): Seq[PHLight] = names.flatMap { name => allLights.find(_.getName.equalsIgnoreCase(name)) }
  def getLightById(id: String): Option[PHLight] = allLights.find(_.getIdentifier.equalsIgnoreCase(id))
  def getLightByName(name: String): Option[PHLight] = allLights.find(_.getName.equalsIgnoreCase(name))
  def lightStates = allLights.map(l => l -> l.getLastKnownLightState).toMap
  def shutdown(): Unit ={
    Option(hueSdk.getSelectedBridge).filter(hueSdk.isHeartbeatEnabled).foreach{ br =>
      hueSdk.disableHeartbeat(br)
      hueSdk.disconnect(br)
    }
    hueSdk.destroySDK()
  }
}
