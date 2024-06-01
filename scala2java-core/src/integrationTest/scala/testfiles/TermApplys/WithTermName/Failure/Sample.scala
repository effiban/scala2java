package testfiles.TermApplys.WithTermName.Failure

import scala.util.Failure

class Sample {
  def foo(): Unit = {
    Failure(new RuntimeException())
  }
}