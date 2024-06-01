package testfiles.Ifs.IfElseIfElse.Basic

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    if (2 < 3) {
      SampleObject.func3(2)
    } else if (9 < 10) {
      SampleObject.func3(9)
    } else {
      SampleObject.func1()
    }
  }
}
