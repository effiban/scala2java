package testfiles.Fors.For.OneEnumerator.WithRangeInRHS.Inclusive

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    for (i <- 1 to 4) {
      SampleObject.func3(i)
    }
  }
}
