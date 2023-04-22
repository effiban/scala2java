package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Lit, Pat, Term}

class PatInterpolateTraverserImplTest extends UnitTestSuite {

  test("traverse") {
    val patInterpolate = Pat.Interpolate(
      prefix = Term.Name("r"),
      parts = List(Lit.String("Hello "), Lit.String(", have a (.+) day")),
      args = List(Term.Name("name"))
    )

    PatInterpolateTraverser.traverse(patInterpolate).structure shouldBe patInterpolate.structure
  }

}
