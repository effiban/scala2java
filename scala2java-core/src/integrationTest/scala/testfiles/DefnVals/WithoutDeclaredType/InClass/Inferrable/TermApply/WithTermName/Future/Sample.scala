package testfiles.DefnVals.WithoutDeclaredType.InClass.Inferrable.TermApply.WithTermName.Future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Sample {
  private val x = Future("abc")
}