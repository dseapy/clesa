package clesa.ha.utils

import java.io.{InputStreamReader, BufferedReader, OutputStreamWriter}
import java.net.{HttpURLConnection, URL}

import com.typesafe.scalalogging.slf4j.Logging

object RestUtils extends Logging {

  def get(urlStr: String): String = {
    logger.info(s"Making GET request to $urlStr")
    val url = new URL(fixUrlString(urlStr))
    val httpCon = url.openConnection().asInstanceOf[HttpURLConnection]
    httpCon.setDoOutput(true)
    httpCon.setRequestMethod("GET")
    httpCon.getInputStream.toString
    val br = new BufferedReader(new InputStreamReader(httpCon.getInputStream))
    Stream.continually(br.readLine()).takeWhile(_ != null).mkString("\n")
  }

  def put(urlStr: String, str: String): String = {
    logger.info(s"Making PUT request to $urlStr with string $str")
    val url = new URL(fixUrlString(urlStr))
    val httpCon = url.openConnection().asInstanceOf[HttpURLConnection]
    httpCon.setDoOutput(true)
    httpCon.setRequestMethod("PUT")
    val out = new OutputStreamWriter(httpCon.getOutputStream)
    out.write(str)
    out.close()
    httpCon.getInputStream.toString
  }

  def fixUrlString(urlStr: String) = urlStr.replaceAllLiterally(" ", "+")
}
