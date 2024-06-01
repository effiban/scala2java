package testfiles.TermApplys.WhenImplicitlyReturned.SecondStatement

import testfilesext.SampleObject

class Sample {
  def foo: Int = {
    SampleObject.func1()
    SampleObject.func5(3)
  }
}