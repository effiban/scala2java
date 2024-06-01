package testfiles.Fors.For.TwoEnumerators

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    val xs = List(1, 2, 3)
    val ys = List(11, 22, 33)
    for (x <- xs;
         y <- ys) {
      SampleObject.func4(x, y)
    }
  }
}
