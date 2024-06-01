package testfiles.TermApplys.WithApplyType.WithTermName.Success

import scala.util.Success

class Sample {
  def foo(): Unit = {
    Success[Int](1)
  }
}