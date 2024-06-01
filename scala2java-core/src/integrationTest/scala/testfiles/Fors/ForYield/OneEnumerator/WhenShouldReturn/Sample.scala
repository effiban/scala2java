package testfiles.Fors.ForYield.OneEnumerator.WhenShouldReturn

import testfilesext.SampleObject

class Sample {

  def foo(): List[Int] = {
    for {
      x <- List(1, 2, 3)
    } yield SampleObject.func5(x)
  }
}
