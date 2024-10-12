package testfiles.TermApplyTypes.WhenInherited.Basic

import testfilesext.SampleParentTrait4

class Sample extends SampleParentTrait4 {

  def foo(): Unit = {
    val x = fooTyped[Int]
  }
}