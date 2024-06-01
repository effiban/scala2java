package testfiles.DefnVals.WithoutDeclaredType.InClass.Inferrable.TermApply.WithApplyType.WithTermName.Try

import scala.util.Try

class Sample {
  private val x = Try[String]("abc")
}