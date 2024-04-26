package dummy

import scala.concurrent.Future

class Sample {
  def foo: Unit = {
    Future.successful("a")
  }
}