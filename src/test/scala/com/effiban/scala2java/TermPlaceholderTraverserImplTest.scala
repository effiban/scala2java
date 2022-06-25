package com.effiban.scala2java

import com.effiban.scala2java.TraversalConstants.JavaPlaceholder

import scala.meta.Term

class TermPlaceholderTraverserImplTest extends UnitTestSuite {

  val termPlaceholderTraverser = new TermPlaceholderTraverserImpl()

  test("testTraverse") {
    termPlaceholderTraverser.traverse(Term.Placeholder())

    outputWriter.toString shouldBe JavaPlaceholder
  }

}
