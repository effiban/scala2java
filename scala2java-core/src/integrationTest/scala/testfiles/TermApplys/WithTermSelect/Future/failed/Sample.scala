package testfiles.TermApplys.WithTermSelect.Future.failed

import scala.concurrent.Future

class Sample {
  def foo(): Unit = {
    Future.failed(new RuntimeException())
  }
}