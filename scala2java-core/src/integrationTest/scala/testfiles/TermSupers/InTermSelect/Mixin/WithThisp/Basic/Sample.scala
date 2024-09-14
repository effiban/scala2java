package testfiles.TermSupers.InTermSelect.Mixin.WithThisp.Basic

import scala.collection.immutable.LinearSeq

class Sample extends LinearSeq[Int] {
  val x: Int = Sample.super.length
}