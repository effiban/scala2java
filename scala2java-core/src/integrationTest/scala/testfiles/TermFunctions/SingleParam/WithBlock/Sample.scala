package testfiles.TermFunctions.SingleParam.WithBlock

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    val f = (x: Int) => {
      SampleObject.func3(x)
      SampleObject.func3(x + 1)
    }
    f(2)
  }
}