package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.DefnVarTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class BlockStatTraverserImplTest extends UnitTestSuite {

  private val TheTermName = q"foo"
  private val TheTraversedTermName = q"traversedFoo"
  
  private val TheTermApply = q"foo()"
  private val TheTraversedTermApply = q"traversedFoo()"

  private val TheDefnVar = q"var x: MyType = 3"
  private val TheTraversedDefnVar = q"var xx: MyTraversedType = 33"

  private val TheDeclVar = q"var x: MyType"
  private val TheTraversedDeclVar = q"var xx: MyTraversedType"


  private val statTermTraverser = mock[StatTermTraverser]
  private val defnVarTraverser = mock[DefnVarTraverser]
  private val declVarTraverser = mock[DeclVarTraverser]

  private val blockStatTraverser = new BlockStatTraverserImpl(
    statTermTraverser,
    defnVarTraverser,
    declVarTraverser
  )


  test("traverse() for a Term.Name") {
    doReturn(TheTraversedTermName).when(statTermTraverser).traverse(eqTree(TheTermName))

    blockStatTraverser.traverse(TheTermName).structure shouldBe TheTraversedTermName.structure
  }

  test("traverse() for a Term.Apply") {
    doReturn(TheTraversedTermApply).when(statTermTraverser).traverse(eqTree(TheTermApply))

    blockStatTraverser.traverse(TheTermApply).structure shouldBe TheTraversedTermApply.structure
  }

  test("traverse() for a Defn.Var") {
    doReturn(DefnVarTraversalResult(TheTraversedDefnVar))
      .when(defnVarTraverser).traverse(eqTree(TheDefnVar), eqTo(StatContext(JavaScope.Block)))

    blockStatTraverser.traverse(TheDefnVar).structure shouldBe TheTraversedDefnVar.structure
  }

  test("traverse() for a Decl.Var") {
    doReturn(TheTraversedDeclVar)
      .when(declVarTraverser).traverse(eqTree(TheDeclVar), eqTo(StatContext(JavaScope.Block)))

    blockStatTraverser.traverse(TheDeclVar).structure shouldBe TheTraversedDeclVar.structure
  }
}

