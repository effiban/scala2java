package testfiles.TermApplys.WithBlockArg.WithSingleStatement

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    SampleObject.func11 {
      SampleObject.x
    }
  }
}