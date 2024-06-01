package testfiles.Fors.ForYield.TwoEnumerators

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    for {
      x <- List(1, 2)
      y <- List(3, 4)
    } yield SampleObject.func4(x, y)
  }
}
