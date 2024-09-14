package testfiles.TermSupers.InTermSelect.Mixin.WithThisp.InnerClass.ClashWithOuterClassParent

import testfilesext.{SampleParentTrait3, SampleParentTrait4}

class Sample extends SampleParentTrait3 {

  class SampleInner extends SampleParentTrait4 {

    val x: Int = Sample.super.doSomething()
  }
}