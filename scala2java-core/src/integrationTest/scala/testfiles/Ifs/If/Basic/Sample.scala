package testfiles.Ifs.If.Basic

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    if (2 < 3) {
      SampleObject.func5(2)
    }
  }
}
