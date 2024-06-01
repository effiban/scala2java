package testfiles.DefnDefs.WithoutDeclaredType.NonInferrable.WithLastStatementType.TermSelect

import testfilesext.SampleObject

class Sample {
  private def foo = {
    SampleObject.func1()
    SampleObject.y
  }
}