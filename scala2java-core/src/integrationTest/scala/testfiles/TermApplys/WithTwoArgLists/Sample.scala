package testfiles.TermApplys.WithTwoArgLists

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    SampleObject.func12(1, 2)(3, 4)
  }
}