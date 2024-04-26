package dummy

import scala.util.Try

class Sample {
  def foo: Unit = {
    Try[Int](1)
  }
}