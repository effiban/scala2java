package testfiles.DefnDefs.WithoutDeclaredType.NonInferrable.WithLastStatementType.TermApply

import testfilesext.SampleObject

class Sample {
  private def foo() = {
    SampleObject.func1()
    SampleObject.func2()
  }
}