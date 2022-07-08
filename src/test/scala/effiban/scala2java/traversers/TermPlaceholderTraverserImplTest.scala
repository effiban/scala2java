package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term

class TermPlaceholderTraverserImplTest extends UnitTestSuite {

  val termPlaceholderTraverser = new TermPlaceholderTraverserImpl()

  test("testTraverse") {
    termPlaceholderTraverser.traverse(Term.Placeholder())

    outputWriter.toString shouldBe JavaPlaceholder
  }

}
