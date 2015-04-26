import org.scalatra.LifeCycle
import javax.servlet.ServletContext
import clesa.ha.MyScalatra

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {

    context mount (new MyScalatra, "/ha/*")
  }
}