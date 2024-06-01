package testfiles.TermApplys.WithTermSelect.Future.successful

import scala.concurrent.Future

class Sample {
  def foo(): Unit = {
    Future.successful("a")
  }
}