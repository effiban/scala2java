package testfiles.DefnVals.WithoutDeclaredType.InClass.Inferrable.TermApply.WithApplyType.WithTermSelect.Future.failed

import scala.concurrent.Future

class Sample {
  private val x = Future.failed[Int](new RuntimeException())
}