package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term

class TermNameTraverserImplTest extends UnitTestSuite {

  private val termNameTraverser = new TermNameTraverserImpl()

  test("traverse") {
    termNameTraverser.traverse(Term.Name("xyz"))

    outputWriter.toString shouldBe "xyz"
  }

}
