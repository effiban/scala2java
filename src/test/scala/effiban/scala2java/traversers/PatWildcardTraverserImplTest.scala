package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Pat

class PatWildcardTraverserImplTest extends UnitTestSuite {

  val patWildcardTraverser = new PatWildcardTraverserImpl()

  test("traverse()") {
    patWildcardTraverser.traverse(Pat.Wildcard())

    outputWriter.toString shouldBe "__"
  }
}
