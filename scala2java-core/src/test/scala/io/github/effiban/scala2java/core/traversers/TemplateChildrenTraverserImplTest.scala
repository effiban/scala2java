package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateChildContext
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.core.traversers.results.matchers.MultiStatTraversalResultScalatestMatcher.equalMultiStatTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Defn, Lit, Name, Tree, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType}

class TemplateChildrenTraverserImplTest extends UnitTestSuite {

  private val TheDeclVar = q"var x: Int"
  private val TheTraversedDeclVar = q"var xx: Int"
  private val TheDeclVarTraversalResult = DeclVarTraversalResult(TheTraversedDeclVar)

  private val TheEnumTypeDef = q"var One, Two = Value"

  private val TheDefnVar = q"var x = 4"
  private val TheTraversedDefnVar = q"var xx = 44"
  private val TheDefnVarTraversalResult = DefnVarTraversalResult(TheTraversedDefnVar)

  private val PrimaryCtorArgs = List(
    param"arg1: Int",
    param"arg2: String"
  )
  private val TraversedPrimaryCtorArgs = List(
    param"arg11: Int",
    param"arg22: String"
  )

  private val SecondaryCtorArgs = List(
    param"arg3: Int",
    param"arg4: String"
  )
  private val TraversedSecondaryCtorArgs = List(
    param"arg33: Int",
    param"arg44: String"
  )

  private val PrimaryCtor = Ctor.Primary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(PrimaryCtorArgs)
  )
  private val PrimaryCtorDefnDef = Defn.Def(
    mods = Nil,
    name = q"MyClass",
    tparams = Nil,
    paramss = List(TraversedPrimaryCtorArgs),
    decltpe = None,
    body = Lit.Unit()
  )
  private val PrimaryCtorTraversalResult = DefnDefTraversalResult(PrimaryCtorDefnDef)

  private val SecondaryCtor = Ctor.Secondary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(SecondaryCtorArgs),
    init = init"this()",
    stats = Nil
  )
  private val TraversedSecondaryCtor = Ctor.Secondary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(TraversedSecondaryCtorArgs),
    init = init"this()",
    stats = Nil
  )
  private val SecondaryCtorTraversalResult = CtorSecondaryTraversalResult(tree = TraversedSecondaryCtor, className = t"MyClass")

  private val TheDefnDef = q"def myMethod(param: Int) = doSomething(param)"
  private val TheTraversedDefnDef = q"def myTraversedMethod(param: Int) = doSomething(param)"
  private val TheDefnDefTraversalResult = DefnDefTraversalResult(TheTraversedDefnDef)

  private val ChildOrder = List[Tree](
    TheDeclVar,
    TheDefnVar,
    PrimaryCtor,
    SecondaryCtor,
    TheDefnDef
  )

  private val childContext = mock[TemplateChildContext]

  private val childTraverser = mock[TemplateChildTraverser]
  private val javaChildOrdering = mock[JavaTemplateChildOrdering]

  private val childrenTraverser = new TemplateChildrenTraverserImpl(childTraverser, javaChildOrdering)

  test("traverse() should return traversed results in order") {

    val children = List[Tree](
      TheDefnDef,
      TheDeclVar,
      SecondaryCtor,
      TheDefnVar,
      PrimaryCtor
    )

    val expectedChildTraversalResults = List[PopulatedStatTraversalResult](
      TheDeclVarTraversalResult,
      TheDefnVarTraversalResult,
      PrimaryCtorTraversalResult,
      SecondaryCtorTraversalResult,
      TheDefnDefTraversalResult
    )
    val expectedTraversalResult = MultiStatTraversalResult(expectedChildTraversalResults)

    expectTraverseDeclVar()
    expectTraverseRegularDefnVar()
    expectTraversePrimaryCtor()
    expectTraverseSecondaryCtor()
    expectTraverseDefnDef()

    expectChildOrdering()

    childrenTraverser.traverse(children, childContext) should equalMultiStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() should skip empty results") {

    val children = List[Tree](
      TheDefnDef,
      TheEnumTypeDef
    )

    val expectedChildTraversalResults = List[PopulatedStatTraversalResult](TheDefnDefTraversalResult)
    val expectedTraversalResult = MultiStatTraversalResult(expectedChildTraversalResults)

    expectTraverseEnumTypeDef()
    expectTraverseDefnDef()

    expectChildOrdering()

    childrenTraverser.traverse(children, childContext) should equalMultiStatTraversalResult(expectedTraversalResult)
  }


  private def expectChildOrdering() = {
    when(javaChildOrdering.compare(any[Tree], any[Tree]))
      .thenAnswer((tree1: Tree, tree2: Tree) => positionOf(tree1) - positionOf(tree2))
  }

  private def expectTraverseDeclVar(): Unit = {
    doReturn(TheDeclVarTraversalResult)
      .when(childTraverser).traverse(eqTree(TheDeclVar), eqTo(childContext))
  }

  private def expectTraverseRegularDefnVar(): Unit = {
    doReturn(TheDefnVarTraversalResult)
      .when(childTraverser).traverse(eqTree(TheDefnVar), eqTo(childContext))
  }

  private def expectTraverseEnumTypeDef(): Unit = {
    doReturn(EmptyStatTraversalResult)
      .when(childTraverser).traverse(eqTree(TheEnumTypeDef), eqTo(childContext))
  }

  private def expectTraversePrimaryCtor(): Unit = {
    doReturn(PrimaryCtorTraversalResult)
      .when(childTraverser).traverse(eqTree(PrimaryCtor), eqTo(childContext))
  }

  private def expectTraverseSecondaryCtor(): Unit = {
    doReturn(SecondaryCtorTraversalResult)
      .when(childTraverser).traverse(eqTree(SecondaryCtor), eqTo(childContext))
  }

  private def expectTraverseDefnDef(): Unit = {
    doReturn(TheDefnDefTraversalResult)
      .when(childTraverser).traverse(eqTree(TheDefnDef), eqTo(childContext))
  }

  private def positionOf(tree: Tree) = {
    ChildOrder.zipWithIndex
      .find { case (child, _) => child.structure == tree.structure }
      .map(_._2)
      .getOrElse(Int.MaxValue)
  }
}
