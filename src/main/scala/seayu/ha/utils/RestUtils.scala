package clesa.ha.utils

import java.io.OutputStreamWriter
import java.net.{HttpURLConnection, URL}

object RestUtils {

  def put(urlStr: String, str: String): String = {
    println(s"Making PUT request to $urlStr with string $str")
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
