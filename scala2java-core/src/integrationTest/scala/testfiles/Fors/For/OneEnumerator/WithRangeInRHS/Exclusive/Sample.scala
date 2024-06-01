package testfiles.Fors.For.OneEnumerator.WithRangeInRHS.Exclusive

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    for (i <- 0 until 4) {
      SampleObject.func3(i)
    }
  }
}
