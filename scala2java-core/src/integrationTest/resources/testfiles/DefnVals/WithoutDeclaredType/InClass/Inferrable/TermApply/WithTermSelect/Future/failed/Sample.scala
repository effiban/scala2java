package dummy

import scala.concurrent.Future

class Sample {
  val x = Future.failed(new RuntimeException())
}