package testfiles.Fors.ForYield.ThreeEnumerators

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    for {
      x <- List(1, 2)
      y <- List(3, 4)
      z <- List(5, 6)
    } yield SampleObject.func6(x, y, z)
  }
}
