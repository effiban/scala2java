package testfiles.TermApplys.WhenInherited.MethodOverriden

import testfilesext.SampleParentTrait3

class Sample extends SampleParentTrait3 {

  def foo(): Unit = {
    val x = doSomething()
  }

  override def doSomething(): Int = 4
}