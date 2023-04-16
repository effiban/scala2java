package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Lit, Pat}

class PatTupleRendererImplTest extends UnitTestSuite {

  val patTupleRenderer = new PatTupleRendererImpl()

  test("traverse()") {
    patTupleRenderer.render(Pat.Tuple(List(Lit.String("myName"), Lit.Int(2), Lit.Boolean(true))))

    outputWriter.toString shouldBe """/* ("myName", 2, true) */"""
  }
}
