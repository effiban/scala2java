package dummy

import scala.util.Failure

class Sample {
  def foo: Unit = {
    Failure(new RuntimeException())
  }
}