package testfiles.DefnVals.WithoutDeclaredType.InClass.Inferrable.TermApply.WithTermSelect.Future.failed

import scala.concurrent.Future

class Sample {
  private val x = Future.failed(new RuntimeException())
}