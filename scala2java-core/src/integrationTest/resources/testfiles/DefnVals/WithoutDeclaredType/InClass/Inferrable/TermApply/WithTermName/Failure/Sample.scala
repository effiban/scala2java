package dummy

import scala.util.Failure

class Sample {
  val x = Failure(new RuntimeException())
}