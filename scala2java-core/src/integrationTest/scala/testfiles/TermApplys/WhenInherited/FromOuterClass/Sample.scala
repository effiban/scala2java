package testfiles.TermApplys.WhenInherited.FromOuterClass

import testfilesext.SampleParentTrait3

class Sample extends SampleParentTrait3 {

  class SampleInner {

    def foo(): Unit = {
      val x = doSomething()
    }
  }
}