package testfiles.TermApplyTypes.WhenInherited.FromOuterClass

import testfilesext.SampleParentTrait4

class Sample extends SampleParentTrait4 {

  class SampleInner {

    def foo(): Unit = {
      val x = fooTyped[Int]
    }
  }
}