package testfiles.TermSupers.InTermSelect.Mixin.WithThisp.InnerClass.Basic

import scala.collection.immutable.LinearSeq

class Sample extends LinearSeq[Int] {

  class SampleInner {
    val x: Int = Sample.super.length
  }
}