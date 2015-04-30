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

  val bridge = hueSdk.getSelectedBridge
  hueSdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL)
  val bridgeCache = bridge.getResourceCache
  val allLights = bridgeCache.getAllLights

  def updateLightState(hueEvent: HueEvent): Unit = {
    allLights.foreach(l => l.setLastKnownLightState(hueEvent.updateState(l.getIdentifier, l.getLastKnownLightState)))
  }

  def increaseBrightnessBy(light: PHLight, inc: Int): Boolean = {
    println(s"Increasing brightness of the ${light.getName} light by $inc")
    val currentBrightness = light.getLastKnownLightState.getBrightness
    println("current brightness: " + currentBrightness)
    val updatedBrightness = currentBrightness + inc
    println("updated brightness: " + updatedBrightness)

    val uls = light.getLastKnownLightState
    if(updatedBrightness < 0){
      if(uls.getBrightness == 0 && !uls.isOn) return false //don't do anything
      setOff(light)
    }
    else {
      val newBrightness = updatedBrightness min 254
      if(uls.getBrightness == newBrightness && uls.isOn) return false //don't do anything
      setBrightnessTo(light, newBrightness)
    }
    true
  }

  def setBrightnessTo(light: PHLight, bri: Int): Boolean = {
    println(s"Setting the brightness of the ${light.getName} light to $bri")
    val updatedLightState = new PHLightState
    updatedLightState.setBrightness(bri)
    updatedLightState.setOn(true)
    bridge.updateLightState(light, updatedLightState, pHLightListener)
    true
  }

  def setOff(light: PHLight): Boolean = {
    println(s"Turning off the ${light.getName} light")
    val updatedLightState = new PHLightState
    updatedLightState.setOn(false)
    bridge.updateLightState(light, updatedLightState, pHLightListener)
    true
  }

  def findLightByNames(names: Seq[String]): Option[PHLight] = findLightsByNames(names).headOption

  def findLightsByNames(names: Seq[String]): Seq[PHLight] = {
    val allMyLights = allLights //to avoid having to query for this information multiple times
    names.flatMap { name =>
      allMyLights.find(_.getName.equalsIgnoreCase(name))
    }
  }

  def getLightById(id: String): Option[PHLight] = {
    allLights.find(_.getIdentifier.equalsIgnoreCase(id))
  }

  def getLightByName(name: String): Option[PHLight] = {
    allLights.find(_.getName.equalsIgnoreCase(name))
  }

  def shutdown(): Unit ={
    Option(hueSdk.getSelectedBridge).filter(hueSdk.isHeartbeatEnabled).foreach{ br =>
      hueSdk.disableHeartbeat(br)
      hueSdk.disconnect(br)
    }
    hueSdk.destroySDK()
  }

  val lightStates = allLights.map(l => l -> l.getLastKnownLightState).toMap

  val pHLightListener = new PHLightListener {
    def onReceivingLights(list: util.List[PHBridgeResource]): Unit = {}
    def onSearchComplete(): Unit = {}
    def onReceivingLightDetails(phLight: PHLight): Unit = {}
    def onError(i: Int, s: String): Unit = errorCallbackFunc(new PHHueError(i, s, ipAddress))
    def onStateUpdate(updatedStateMap: util.Map[String, String], list: util.List[PHHueError]): Unit = {
      HueEvent(updatedStateMap).foreach(eventCallbackFunc)
      println(list)
    }
    def onSuccess(): Unit = {}
  }
}
