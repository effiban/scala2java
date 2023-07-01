package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.DeclVarTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class BlockStatTraverserImplTest extends UnitTestSuite {

  private val TheTermName = q"foo"
  private val TheTraversedTermName = q"traversedFoo"
  
  private val TheTermApply = q"foo()"
  private val TheTraversedTermApply = q"traversedFoo()"

  private val TheDeclVar = q"var x: MyType"
  private val TheTraversedDeclVar = q"var xx: MyTraversedType"

  private val expressionTermRefTraverser = mock[ExpressionTermRefTraverser]
  private val defaultTermTraverser = mock[DefaultTermTraverser]
  private val declVarTraverser = mock[DeclVarTraverser]

  private val blockStatTraverser = new BlockStatTraverserImpl(
    expressionTermRefTraverser,
    defaultTermTraverser,
    declVarTraverser
  )


  test("traverse() for a Term.Name") {
    doReturn(TheTraversedTermName).when(expressionTermRefTraverser).traverse(eqTree(TheTermName))

    blockStatTraverser.traverse(TheTermName).structure shouldBe TheTraversedTermName.structure
  }

  test("traverse() for a Term.Apply") {
    doReturn(TheTraversedTermApply).when(defaultTermTraverser).traverse(eqTree(TheTermApply))

    blockStatTraverser.traverse(TheTermApply).structure shouldBe TheTraversedTermApply.structure
  }

  test("traverse() for a Decl.Var") {
    doReturn(DeclVarTraversalResult(TheTraversedDeclVar))
      .when(declVarTraverser).traverse(eqTree(TheDeclVar), eqTo(StatContext(JavaScope.Block)))

    blockStatTraverser.traverse(TheDeclVar).structure shouldBe TheTraversedDeclVar.structure
  }
}

