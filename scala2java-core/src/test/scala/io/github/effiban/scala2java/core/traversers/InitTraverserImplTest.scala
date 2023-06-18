package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Init, Name, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InitTraverserImplTest extends UnitTestSuite {

  private val TypeName = t"MyType"
  private val TraversedTypeName = t"MyTraversedType"

  private val Arg1 = q"arg1"
  private val Arg2 = q"arg2"
  private val ArgList1 = List(Arg1, Arg2)

  private val Arg3 = q"arg3"
  private val Arg4 = q"arg4"
  private val ArgList2 = List(Arg3, Arg4)

  private val TraversedArg1 = q"arg11"
  private val TraversedArg2 = q"arg22"
  private val TraversedArgList1 = List(TraversedArg1, TraversedArg2)

  private val TraversedArg3 = q"arg33"
  private val TraversedArg4 = q"arg44"
  private val TraversedArgList2 = List(TraversedArg3, TraversedArg4)

  private val typeTraverser = mock[TypeTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val initTraverser = new InitTraverserImpl(
    typeTraverser,
    expressionTermTraverser
  )

  test("traverse() with no arguments") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = Nil)
    val traversedInit = Init(tpe = TraversedTypeName, name = Name.Anonymous(), argss = Nil)

    doReturn(TraversedTypeName).when(typeTraverser).traverse(eqTree(TypeName))

    initTraverser.traverse(init).structure shouldBe traversedInit.structure
  }

  test("traverse() for one argument list") {
    val init = Init(tpe = TypeName, name = Name.Anonymous(), argss = List(ArgList1))
    val traversedInit = Init(tpe = TraversedTypeName, name = Name.Anonymous(), argss = List(TraversedArgList1))

    doReturn(TraversedTypeName).when(typeTraverser).traverse(eqTree(TypeName))
    doAnswer((arg: Term) => arg match {
      case anArg if anArg.structure == Arg1.structure => TraversedArg1
      case anArg if anArg.structure == Arg2.structure => TraversedArg2
      case anArg => anArg
    }).when(expressionTermTraverser).traverse(any[Term])

    initTraverser.traverse(init).structure shouldBe traversedInit.structure
  }

  test("traverse() for two argument lists") {

    val init = Init(
      tpe = TypeName,
      name = Name.Anonymous(),
      argss = List(ArgList1, ArgList2)
    )
    val traversedInit = Init(
      tpe = TraversedTypeName,
      name = Name.Anonymous(),
      argss = List(TraversedArgList1, TraversedArgList2)
    )

    doReturn(TraversedTypeName).when(typeTraverser).traverse(eqTree(TypeName))
    doAnswer((arg: Term) => arg match {
      case anArg if anArg.structure == Arg1.structure => TraversedArg1
      case anArg if anArg.structure == Arg2.structure => TraversedArg2
      case anArg if anArg.structure == Arg3.structure => TraversedArg3
      case anArg if anArg.structure == Arg4.structure => TraversedArg4
      case anArg => anArg
    }).when(expressionTermTraverser).traverse(any[Term])

    initTraverser.traverse(init).structure shouldBe traversedInit.structure
  }
}
