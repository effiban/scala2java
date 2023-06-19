package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class BlockStatTraverserImplTest extends UnitTestSuite {

  private val TheTermName = q"foo"
  private val TheTraversedTermName = q"traversedFoo"
  
  private val TheTermApply = q"foo()"
  private val TheTraversedTermApply = q"traversedFoo()"
  
  private val expressionTermRefTraverser = mock[ExpressionTermRefTraverser]
  private val defaultTermTraverser = mock[DefaultTermTraverser]

  private val blockStatTraverser = new BlockStatTraverserImpl(
    expressionTermRefTraverser,
    defaultTermTraverser
  )


  test("traverse() for a Term.Name") {
    doReturn(TheTraversedTermName).when(expressionTermRefTraverser).traverse(eqTree(TheTermName))

    blockStatTraverser.traverse(TheTermName).structure shouldBe TheTraversedTermName.structure
  }

  test("traverse() for a Term.Apply") {
    doReturn(TheTraversedTermApply).when(defaultTermTraverser).traverse(eqTree(TheTermApply))

    blockStatTraverser.traverse(TheTermApply).structure shouldBe TheTraversedTermApply.structure
  }
}

