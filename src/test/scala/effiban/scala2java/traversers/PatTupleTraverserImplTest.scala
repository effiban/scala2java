package effiban.scala2java.traversers

import effiban.scala2java.UnitTestSuite

import scala.meta.{Lit, Pat}

class PatTupleTraverserImplTest extends UnitTestSuite {

  val patTupleTraverser = new PatTupleTraverserImpl()

  test("traverse()") {
    patTupleTraverser.traverse(Pat.Tuple(List(Lit.String("myName"), Lit.Int(2), Lit.Boolean(true))))

    outputWriter.toString shouldBe """/* ("myName", 2, true) */"""
  }
}
