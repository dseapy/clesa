package clesa.ha.hue

import com.philips.lighting.hue.sdk.{PHAccessPoint, PHHueSDK}
import com.philips.lighting.model.PHLight
import com.typesafe.config.Config
import collection.JavaConversions._

case class HueConnector(ip: String,
                        username: String,
                        appName: String,
                        deviceName: String) {

  verifyConnected()

  lazy val hueSdk = {
    val hSdk = PHHueSDK.create()
    hSdk.setAppName(appName)
    hSdk.setDeviceName(deviceName)
    hSdk
  }

  lazy val accessPoint = {
    val ap = new PHAccessPoint()
    ap.setIpAddress(ip)
    ap.setUsername(username)
    ap
  }

  def verifyConnected() = if(!hueSdk.isAccessPointConnected(accessPoint)){
    //sleep for a second after connecting to verify it's up before sending it messages... may be better way
    hueSdk.connect(accessPoint)
    Thread.sleep(1000L)
  }

  def bridge = {
    verifyConnected()
    hueSdk.getSelectedBridge
  }
  def bridgeCache = bridge.getResourceCache
  def allLights = bridgeCache.getAllLights

  def convertPercentToRGB(per: Int): Int = (per.toDouble*2.55).toInt

  def increaseBrightnessByPercent(light: PHLight, perInc: Int): PHLight = {
    println(s"Increasing brightness of the ${light.getName} light by $perInc")
    val currentBrightness = light.getLastKnownLightState.getBrightness
    val updatedBrightness = {
      val unscaled = currentBrightness + convertPercentToRGB(perInc)
      if(unscaled > 255) 255
      else if(unscaled < 0) 0
      else unscaled
    }
    val updatedLightState = {
      val uls = light.getLastKnownLightState
      uls.setBrightness(updatedBrightness)
      uls.setOn(true)
      uls
    }
    bridge.updateLightState(light, updatedLightState)
    light
  }

  def setBrightnessByPercent(light: PHLight, per: Int): PHLight = {
    println(s"Setting the brightness of the ${light.getName} light to $per")
    val currentState = light.getLastKnownLightState
    val updatedState = {
      currentState.setBrightness(convertPercentToRGB(per))
      currentState.setOn(true)
      currentState
    }
    bridge.updateLightState(light, updatedState)
    light
  }

  def setOff(light: PHLight) = {
    println(s"Turning off the ${light.getName} light")
    val currentState = light.getLastKnownLightState
    val updatedState = {
      currentState.setOn(false)
      currentState
    }
    bridge.updateLightState(light, updatedState)
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
}

case object HueConnector {
  def apply(config: Config): HueConnector = {
    val hueConfig = config.getConfig("clesa.ha.hue")
    val bridgeIp = hueConfig.getString("bridge-ip-address")
    val username = hueConfig.getString("username")
    val appName = hueConfig.getString("appName")
    val deviceName = hueConfig.getString("deviceName")
    HueConnector(bridgeIp, username, appName, deviceName)
  }
}