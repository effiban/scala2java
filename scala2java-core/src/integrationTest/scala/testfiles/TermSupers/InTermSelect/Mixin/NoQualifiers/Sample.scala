package testfiles.TermSupers.InTermSelect.Mixin.NoQualifiers

import scala.collection.immutable.LinearSeq

class Sample extends LinearSeq[Int] {
  val x: Int = super.length
}