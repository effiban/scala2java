package testfiles.Fors.ForYield.OneEnumerator.WhenShouldNotReturn

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    for {
      x <- List(1, 2, 3)
    } yield SampleObject.func3(x)
  }
}
