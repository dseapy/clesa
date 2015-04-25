package seayu.ha

import java.io.OutputStreamWriter
import java.net.{HttpURLConnection, URL}

import scala.util.Try

trait Task {
  def id: String
  def execute: String
  def put(urlStr: String, str: String): String = {
    println(urlStr)
    val url = new URL(urlStr)
    val httpCon = url.openConnection().asInstanceOf[HttpURLConnection]

    httpCon.setDoOutput(true)
    httpCon.setRequestMethod("PUT")
    val out = new OutputStreamWriter(httpCon.getOutputStream)
    out.write(str)
    out.close()
    httpCon.getInputStream.toString
  }
}

trait ChildTask extends Task

trait ParentTask extends Task {
  def exChildren: Seq[Task]
  def execute = exChildren.map(ht => s"${ht.id} responded with: ${ht.execute}").mkString("\n")
}

case object Task {
  def apply(originalTaskString: String): Task = {
    val taskString = cleanText(originalTaskString)
    lazy val taskStringArray = taskString.split(" ")

    //hue
    val lightMentioned = taskString.contains("light")
    val lightId = if(taskString.contains("tv")) "1" else if(taskString.contains("entry")) "2" else ""
    println(taskString)
    if(lightId.nonEmpty && taskString.contains("percent") && lightMentioned){
      Try{
        val briString = taskStringArray(taskStringArray.indexOf("percent") - 1)
        val cleanedBriString = if(briString.startsWith("2") && briString.toInt > 100) briString.replaceFirst("2","") else briString
        val bri = (cleanedBriString.toInt.toDouble / 100.0 * 255.0).toInt
        HueLightTask(lightId, "{\"on\":true, \"bri\":" + bri + "}")
      }.getOrElse(EmptyTask)
    }
    else if(taskString.contains("on") && lightId.nonEmpty && lightMentioned) HueLightTask(lightId, "{\"on\":true}")
    else if(taskString.contains("off") && lightId.nonEmpty && lightMentioned) HueLightTask(lightId, "{\"on\":false}")
    else EmptyTask
  }

  def cleanText(originalTaskString: String): String = {
    originalTaskString.replace("%", " percent").replace("  "," ")
  }
}

case class HueLightTask(lightId: String, jsonStr: String) extends Task {
  def id = lightId ++ jsonStr
  def execute = {
    put(s"http://192.168.0.152/api/newdeveloper/lights/$lightId/state", jsonStr)
  }
}

case object EmptyTask extends Task {
  val id = "EmptyTask"
  def execute = ""
}