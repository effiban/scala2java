package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Lit, Pat, Term}

class PatInterpolateTraverserImplTest extends UnitTestSuite {

  private val patInterpolateTraverser = new PatInterpolateTraverserImpl()

  test("traverse") {
    val patInterpolate = Pat.Interpolate(
      prefix = Term.Name("r"),
      parts = List(Lit.String("Hello "), Lit.String(", have a (.+) day")),
      args = List(Term.Name("name"))
    )

    patInterpolateTraverser.traverse(patInterpolate)

    outputWriter.toString shouldBe """/* r"Hello ${`name`}, have a (.+) day" */"""
  }

}
