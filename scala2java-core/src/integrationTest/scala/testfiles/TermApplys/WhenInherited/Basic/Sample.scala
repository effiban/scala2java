package testfiles.TermApplys.WhenInherited.Basic

import testfilesext.SampleParentTrait3

class Sample extends SampleParentTrait3 {

  def foo(): Unit = {
    val x = doSomething()
  }
}