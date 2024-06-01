package testfiles.Ifs.IfElse.WithImplicitReturn

import testfilesext.SampleObject

class Sample {

  def foo(): Int = {
    if (2 < 3) {
      SampleObject.func5(2)
    } else {
      SampleObject.func5(3)
    }
  }
}
