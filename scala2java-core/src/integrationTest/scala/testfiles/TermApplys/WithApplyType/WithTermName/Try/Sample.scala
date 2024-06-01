package testfiles.TermApplys.WithApplyType.WithTermName.Try

import scala.util.Try

class Sample {
  def foo(): Unit = {
    Try[Int](1)
  }
}