package testfiles.TermApplys.WithArgsOfPreDefTypes.Tuple.OfTwo

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    SampleObject.func10((1, 2))
  }
}