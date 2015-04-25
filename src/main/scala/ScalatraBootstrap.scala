import org.scalatra.LifeCycle
import javax.servlet.ServletContext
import seayu.ha.MyScalatra

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {

    context mount (new MyScalatra, "/ha/*")
  }
}