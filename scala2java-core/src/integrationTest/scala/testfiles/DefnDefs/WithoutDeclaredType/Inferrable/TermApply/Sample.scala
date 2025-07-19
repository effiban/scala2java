package testfiles.DefnDefs.WithoutDeclaredType.Inferrable.TermApply

import testfilesext.SampleObject

class Sample {
  //noinspection TypeAnnotation
  private def foo() = {
    SampleObject.func1()
    SampleObject.func2()
  }
}