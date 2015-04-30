package clesa.ha.components.input.voice

import clesa.ha.components.inputoutput.hue.HueEndpoint

import scala.util.Try

case object Task {
  lazy val hueConnector = HueEndpoint("192.168.0.152", "hue")
  def apply(originalTaskString: String): Unit = {
    val taskString = cleanText(originalTaskString)
    lazy val taskStringArray = taskString.split(" ")

    //hue
    val lightMentioned = taskString.contains("light")
    val lightOption = hueConnector.findLightByNames(taskStringArray)
    println(taskString)

    if(lightOption.nonEmpty && taskString.contains("percent") && lightMentioned){
      Try{
        val briString = taskStringArray(taskStringArray.indexOf("percent") - 1)
        val cleanedBriString = if(briString.startsWith("2") && briString.toInt > 100) briString.replaceFirst("2","") else briString
        val bri = cleanedBriString.toInt
        if((taskString contains "by") && ((taskString contains "increase") || (taskString contains "up")))
          hueConnector.increaseBrightnessByPercent(lightOption.get, bri)
        else if((taskString contains "by") && (taskString contains "decrease") || (taskString contains "down"))
          hueConnector.increaseBrightnessByPercent(lightOption.get, -bri)
        else
          hueConnector.setBrightnessToPercent(lightOption.get, bri)
      }
    }
    else if(taskString.contains("on") && lightOption.nonEmpty && lightMentioned) hueConnector.setBrightnessToPercent(lightOption.get, 100)
    else if(taskString.contains("off") && lightOption.nonEmpty && lightMentioned) hueConnector.setOff(lightOption.get)
  }

  def cleanText(originalTaskString: String): String = {
    originalTaskString.replace("%", " percent").replace("  "," ")
  }
}