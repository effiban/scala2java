package testfiles.DefnDefs.WithoutDeclaredType.Inferrable.TermSelect

import testfilesext.SampleObject

class Sample {
  private def foo = {
    SampleObject.func1()
    SampleObject.y
  }
}