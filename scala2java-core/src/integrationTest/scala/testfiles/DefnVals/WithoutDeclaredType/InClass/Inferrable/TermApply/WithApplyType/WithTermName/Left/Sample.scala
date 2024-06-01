package testfiles.DefnVals.WithoutDeclaredType.InClass.Inferrable.TermApply.WithApplyType.WithTermName.Left

class Sample {
  private val x = Left[Exception, String](new RuntimeException())
}