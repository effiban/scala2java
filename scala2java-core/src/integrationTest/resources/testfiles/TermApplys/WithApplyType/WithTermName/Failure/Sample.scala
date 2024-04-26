package dummy

import scala.util.Failure

class Sample {
  def foo: Unit = {
    Failure[Int](new RuntimeException())
  }
}