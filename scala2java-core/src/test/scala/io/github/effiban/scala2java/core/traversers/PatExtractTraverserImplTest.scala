package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatExtractRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Pat, Term}

class PatExtractTraverserImplTest extends UnitTestSuite {

  private val patExtractRenderer = mock[PatExtractRenderer]
  private val patExtractTraverser = new PatExtractTraverserImpl(patExtractRenderer)

  test("traverse") {
    val patExtract = Pat.Extract(
      fun = Term.Name("MyClass"),
      args = List(Pat.Var(Term.Name("x")), Pat.Var(Term.Name("y")))
    )
    patExtractTraverser.traverse(patExtract)

    verify(patExtractRenderer).render(eqTree(patExtract))
  }

}
