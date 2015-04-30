package clesa.ha.components

import java.util

import com.philips.lighting.hue.listener.PHLightListener
import com.philips.lighting.hue.sdk.{PHAccessPoint, PHHueSDK}
import com.philips.lighting.model.{PHBridgeResource, PHHueError, PHLight}

import scala.collection.JavaConversions._

class Hue(ipAddress: String,
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

  def convertPercentToRGB(per: Int): Int = (per.toDouble*2.55).toInt

  def increaseBrightnessByPercent(light: PHLight, perInc: Int): PHLight = {
    println(s"Increasing brightness of the ${light.getName} light by $perInc")
    val currentBrightness = light.getLastKnownLightState.getBrightness
    println("current brightness: " + currentBrightness)
    val updatedBrightness = currentBrightness + convertPercentToRGB(perInc)
    println("updated brightness: " + updatedBrightness)
    val updatedLightState = {
      val uls = light.getLastKnownLightState
      if(updatedBrightness < 0){
        if(uls.getBrightness == 0 && !uls.isOn) return light //don't do anything
        uls.setBrightness(0)
        uls.setOn(false)
        uls
      }
      else {
        val newBrightness = updatedBrightness min 255
        if(uls.getBrightness == newBrightness && uls.isOn) return light //don't do anything
        uls.setBrightness(newBrightness)
        uls.setOn(true)
        uls
      }
    }
    bridge.updateLightState(light, updatedLightState, pHLightListener)
    light
  }

  def setBrightnessToPercent(light: PHLight, per: Int): PHLight = {
    println(s"Setting the brightness of the ${light.getName} light to $per")
    val currentState = light.getLastKnownLightState
    val updatedState = {
      currentState.setBrightness(convertPercentToRGB(per))
      currentState.setOn(true)
      currentState
    }
    bridge.updateLightState(light, updatedState, pHLightListener)
    light
  }

  def setOff(light: PHLight) = {
    println(s"Turning off the ${light.getName} light")
    val currentState = light.getLastKnownLightState
    val updatedState = {
      currentState.setOn(false)
      currentState
    }
    bridge.updateLightState(light, updatedState, pHLightListener)
    light
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

  val pHLightListener = new PHLightListener {
    override def onReceivingLights(list: util.List[PHBridgeResource]): Unit = println("on receiving lights:" + list)
    override def onSearchComplete(): Unit = println("search complete: ")
    override def onReceivingLightDetails(phLight: PHLight): Unit = println("on receiving light details:" + phLight)
    override def onError(i: Int, s: String): Unit = println("error: " + i + " " + s)
    override def onStateUpdate(map: util.Map[String, String], list: util.List[PHHueError]): Unit = println("on status update: " + map + list)
    override def onSuccess(): Unit = println("success")
  }
}
