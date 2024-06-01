package testfiles.TermApplys.WithArgsOfPreDefTypes.Tuple.OfThree

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    SampleObject.func9((1, 2, 3))
  }
}