package testfiles.TermApplys.WithBlockArg.WithMultipleStatements.ReturningInt

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    SampleObject.func11 {
      SampleObject.func1()
      SampleObject.func2()
      3
    }
  }
}