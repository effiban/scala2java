package testfiles.TermApplys.WithNamedArguments

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    SampleObject.func4(x = 1, y = 2)
  }
}