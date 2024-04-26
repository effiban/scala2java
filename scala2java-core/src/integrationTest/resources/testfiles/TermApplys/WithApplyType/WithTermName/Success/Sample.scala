package dummy

import scala.util.Success

class Sample {
  def foo: Unit = {
    Success[Int](1)
  }
}