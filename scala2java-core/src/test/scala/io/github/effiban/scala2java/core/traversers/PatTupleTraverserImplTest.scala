package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatTupleRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Pat}

class PatTupleTraverserImplTest extends UnitTestSuite {

  private val patTupleRenderer = mock[PatTupleRenderer]
  private val patTupleTraverser = new PatTupleTraverserImpl(patTupleRenderer)

  test("traverse()") {
    val patTuple = Pat.Tuple(List(Lit.String("myName"), Lit.Int(2), Lit.Boolean(true)))

    patTupleTraverser.traverse(patTuple)

    verify(patTupleRenderer).render(eqTree(patTuple))
  }
}
