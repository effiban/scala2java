package testfiles.DefnVals.WithoutDeclaredType.InClass.Inferrable.TermApply.WithTermName.Failure

import scala.util.Failure

class Sample {
  private val x = Failure(new RuntimeException())
}