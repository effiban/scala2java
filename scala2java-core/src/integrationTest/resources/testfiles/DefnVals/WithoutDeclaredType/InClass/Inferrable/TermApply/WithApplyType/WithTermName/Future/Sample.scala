package dummy

import scala.concurrent.Future

class Sample {
  val x = Future[String]("abc")
}