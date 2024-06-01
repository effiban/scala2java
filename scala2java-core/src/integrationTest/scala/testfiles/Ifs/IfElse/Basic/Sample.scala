package testfiles.Ifs.IfElse.Basic

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    if (2 < 3) {
      SampleObject.func3(2)
    } else {
      SampleObject.func3(3)
    }
  }
}
