package testfiles.TermApplys.WithTermName.Future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Sample {
  def foo(): Unit = {
    Future(1)
  }
}