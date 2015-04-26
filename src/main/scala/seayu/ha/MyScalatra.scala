package clesa.ha

import org.scalatra._
import org.scalatra.scalate.ScalateSupport


class MyScalatra extends ScalatraServlet with ScalateSupport {

  get("/") {
    <html>
      <h1>clesa Home Automation Server</h1>
	  </html>
  }

  get("/request") {
    val haTask = Task(params("query"))
    <html>
      <h1>Your query was {params("query")}</h1>
    </html>
  }
}