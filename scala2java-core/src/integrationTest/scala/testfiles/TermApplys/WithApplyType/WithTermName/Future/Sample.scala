package testfiles.TermApplys.WithApplyType.WithTermName.Future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Sample {
  def foo(): Unit = {
    Future[Int](1)
  }
}