package testfiles.TermSupers.InTermSelect.Mixin.WithSuperp

import scala.collection.immutable.LinearSeq

class Sample extends LinearSeq[Int] {
  val x: Int = super[LinearSeq].length
}