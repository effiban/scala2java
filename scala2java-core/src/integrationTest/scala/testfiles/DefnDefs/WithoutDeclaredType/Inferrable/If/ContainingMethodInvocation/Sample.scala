package testfiles.DefnDefs.WithoutDeclaredType.Inferrable.If.ContainingMethodInvocation

import testfilesext.SampleObject

class Sample {
  private def foo() = {
    SampleObject.func1()
    if (2 < 3) {
      SampleObject.func2()
    } else {
      SampleObject.func3(3)
    }
  }
}