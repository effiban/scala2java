package testfiles.TermApplys.WhenImplicitlyReturned.OnlyStatement

import testfilesext.SampleObject

class Sample {
  def foo: Int = {
    SampleObject.func5(3)
  }
}