package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatInterpolateRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Pat, Term}

class PatInterpolateTraverserImplTest extends UnitTestSuite {

  private val patInterpolateRenderer = mock[PatInterpolateRenderer]
  private val patInterpolateTraverser = new PatInterpolateTraverserImpl(patInterpolateRenderer)

  test("traverse") {
    val patInterpolate = Pat.Interpolate(
      prefix = Term.Name("r"),
      parts = List(Lit.String("Hello "), Lit.String(", have a (.+) day")),
      args = List(Term.Name("name"))
    )

    patInterpolateTraverser.traverse(patInterpolate)

    verify(patInterpolateRenderer).render(eqTree(patInterpolate))
  }

}
