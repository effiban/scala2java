package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Lit, Pat}

class PatTupleTraverserImplTest extends UnitTestSuite {

  test("traverse()") {
    val patTuple = Pat.Tuple(List(Lit.String("myName"), Lit.Int(2), Lit.Boolean(true)))

    PatTupleTraverser.traverse(patTuple).structure shouldBe patTuple.structure
  }
}
