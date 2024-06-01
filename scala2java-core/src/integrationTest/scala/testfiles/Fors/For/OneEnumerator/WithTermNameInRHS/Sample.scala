package testfiles.Fors.For.OneEnumerator.WithTermNameInRHS

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    val xs = List(1, 2, 3)
    for (x <- xs) {
      SampleObject.func3(x)
    }
  }
}
