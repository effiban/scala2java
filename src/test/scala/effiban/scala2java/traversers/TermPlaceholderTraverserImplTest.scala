package effiban.scala2java.traversers

import effiban.scala2java.TraversalConstants.JavaPlaceholder
import effiban.scala2java.UnitTestSuite

import scala.meta.Term

class TermPlaceholderTraverserImplTest extends UnitTestSuite {

  val termPlaceholderTraverser = new TermPlaceholderTraverserImpl()

  test("testTraverse") {
    termPlaceholderTraverser.traverse(Term.Placeholder())

    outputWriter.toString shouldBe JavaPlaceholder
  }

}
