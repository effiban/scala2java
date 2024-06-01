package testfiles.TermFunctions.TwoParams.Basic

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    val f = (x: Int, y: Int) => SampleObject.func4(x, y)
    f(2, 3)
  }
}