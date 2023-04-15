package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Lit, Pat, Term}

class PatInterpolateRendererImplTest extends UnitTestSuite {

  private val patInterpolateRenderer = new PatInterpolateRendererImpl()

  test("traverse") {
    val patInterpolate = Pat.Interpolate(
      prefix = Term.Name("r"),
      parts = List(Lit.String("Hello "), Lit.String(", have a (.+) day")),
      args = List(Term.Name("name"))
    )

    patInterpolateRenderer.render(patInterpolate)

    outputWriter.toString shouldBe """/* r"Hello ${`name`}, have a (.+) day" */"""
  }

}
