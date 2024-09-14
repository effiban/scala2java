package testfiles.TermSupers.InTermSelect.Mixin.WithThisp.InnerClass.ClashWithOuterClass

import scala.collection.immutable.LinearSeq

class Sample {

  def size(): Int = 3

  class SampleInner extends LinearSeq[Int] {

    val x: Int = super.length
  }
}