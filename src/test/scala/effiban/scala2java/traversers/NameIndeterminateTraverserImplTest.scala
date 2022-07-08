package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Name

class NameIndeterminateTraverserImplTest extends UnitTestSuite {

  val nameIndeterminateTraverser = new NameIndeterminateTraverserImpl()

  test("traverse()") {
    nameIndeterminateTraverser.traverse(Name.Indeterminate("myName"))
    outputWriter.toString shouldBe "myName"
  }
}
