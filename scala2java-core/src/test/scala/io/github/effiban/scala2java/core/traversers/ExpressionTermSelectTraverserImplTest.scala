package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm}

class ExpressionTermSelectTraverserImplTest extends UnitTestSuite {

  private val MyInstance = q"MyObject"
  private val MyTraversedInstance = q"MyTraversedObject"
  private val MyMethod = Term.Name("myMethod")
  private val MyScalaSelect = Term.Select(qual = MyInstance, name = MyMethod)
  private val MyTraversedScalaSelect = Term.Select(qual = MyTraversedInstance, name = MyMethod)

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val expressionTermSelectTraverser = new ExpressionTermSelectTraverserImpl(expressionTermTraverser)

  test("traverse()") {
    doReturn(MyTraversedInstance).when(expressionTermTraverser).traverse(eqTree(MyInstance))

    expressionTermSelectTraverser.traverse(MyScalaSelect).structure shouldBe MyTraversedScalaSelect.structure
  }
}
