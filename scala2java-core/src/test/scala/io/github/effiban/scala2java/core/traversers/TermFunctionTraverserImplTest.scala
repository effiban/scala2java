package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, StatContext}
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

class TermFunctionTraverserImplTest extends UnitTestSuite {

  private val LambdaStatContext = StatContext(JavaScope.LambdaSignature)

  private val termParamTraverser = mock[TermParamTraverser]
  private val defaultBlockTraverser = mock[DefaultBlockTraverser]
  private val defaultTermTraverser = mock[DefaultTermTraverser]

  private val termFunctionTraverser = new TermFunctionTraverserImpl(
    termParamTraverser,
    defaultBlockTraverser,
    defaultTermTraverser
  )


  test("traverse with zero params and one term") {
    val body = q"doSomething()"
    val traversedBody = q"doSomethingElse()"

    val function = Term.Function(Nil, body)
    val traversedFunction = Term.Function(Nil, traversedBody)

    doReturn(traversedBody).when(defaultTermTraverser).traverse(eqTree(body))

    termFunctionTraverser.traverse(function).structure shouldBe traversedFunction.structure
  }

  test("traverse with one param and one term") {
    val param = param"arg: Int"
    val traversedParam = param"argg: Int"

    val body = q"doSomething(arg)"
    val traversedBody = q"doSomethingg(argg)"

    val function = Term.Function(params = List(param), body = body)
    val traversedFunction = Term.Function(List(traversedParam), traversedBody)

    doReturn(traversedParam).when(termParamTraverser).traverse(eqTree(param), eqTo(LambdaStatContext))
    doReturn(traversedBody).when(defaultTermTraverser).traverse(eqTree(body))

    termFunctionTraverser.traverse(function).structure shouldBe traversedFunction.structure
  }

  test("traverse with two params and one term") {
    val param1 = param"arg1: Int"
    val traversedParam1 = param"arg11: Int"
    val param2 = param"arg2: Int"
    val traversedParam2 = param"arg22: Int"

    val body = q"doSomething(arg1, arg2)"
    val traversedBody = q"doSomethingg(arg11, arg22)"

    val function = Term.Function(List(param1, param2), body)
    val traversedFunction = Term.Function(List(traversedParam1, traversedParam2), traversedBody)

    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == param1.structure => traversedParam1
      case aParam if aParam.structure == param2.structure => traversedParam2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(LambdaStatContext))

    doReturn(traversedBody).when(defaultTermTraverser).traverse(eqTree(body))

    termFunctionTraverser.traverse(function).structure shouldBe traversedFunction.structure
  }

  test("traverse with one param and a block of one term") {
    val param = param"arg: Int"
    val traversedParam = param"traversedArg: Int"

    val bodyAsTerm = q"doSomething(arg)"
    val bodyAsBlock =
      q"""
      {
        doSomething(arg)
      }
      """
    val traversedBody = q"doSomethingg(traversedArg)"

    val function = Term.Function(List(param), bodyAsBlock)
    val traversedFunction = Term.Function(List(traversedParam), traversedBody)

    doReturn(traversedParam).when(termParamTraverser).traverse(eqTree(param), eqTo(LambdaStatContext))
    doReturn(traversedBody).when(defaultTermTraverser).traverse(eqTree(bodyAsTerm))

    termFunctionTraverser.traverse(function).structure shouldBe traversedFunction.structure  }

  test("traverse with one param and a block of one non-term stat") {
    val param = param"arg: Int"
    val traversedParam = param"traversedArg: Int"

    val body =
      q"""
      {
        val y = arg
      }
      """
    val traversedBody =
      q"""
      {
        val yy = traversedArg
      }
      """

    val function = Term.Function(List(param), body)

    val expectedFunctionTraversalResult = Term.Function(List(traversedParam), traversedBody)

    doReturn(traversedParam).when(termParamTraverser).traverse(eqTree(param), eqTo(LambdaStatContext))
    doReturn(traversedBody)
      .when(defaultBlockTraverser).traverse(eqTree(body), eqBlockContext(BlockContext(shouldReturnValue = No))
    )

    termFunctionTraverser.traverse(function).structure shouldBe expectedFunctionTraversalResult.structure
  }

  test("traverse with one param and a block of two stats") {
    val param = param"arg: Int"
    val traversedParam = param"traversedArg: Int"

    val body =
      q"""
      doSomething(arg)
      3
      """
    val traversedBody =
      q"""
      doSomethingg(traversedArg)
      33
      """

    val function = Term.Function(List(param), body)

    val expectedFunctionTraversalResult = Term.Function(List(traversedParam), traversedBody)

    doReturn(traversedParam).when(termParamTraverser).traverse(eqTree(param), eqTo(LambdaStatContext))
    doReturn(traversedBody)
      .when(defaultBlockTraverser).traverse(eqTree(body), eqBlockContext(BlockContext(shouldReturnValue = No))
    )

    termFunctionTraverser.traverse(function).structure shouldBe expectedFunctionTraversalResult.structure
  }

  test("traverse with one param and a block when shouldBodyReturnValue='Yes'") {
    val param = param"arg: Int"
    val traversedParam = param"traversedArg: Int"

    val body =
      q"""
      doSomething(arg)
      3
      """
    val traversedBody =
      q"""
      doSomethingg(traversedArg)
      33
      """

    val function = Term.Function(List(param), body)

    val expectedFunctionTraversalResult = Term.Function(List(traversedParam), traversedBody)

    doReturn(traversedParam).when(termParamTraverser).traverse(eqTree(param), eqTo(LambdaStatContext))
    doReturn(traversedBody)
      .when(defaultBlockTraverser).traverse(eqTree(body), eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    val actualResult = termFunctionTraverser.traverse(function, shouldBodyReturnValue = Yes)
    actualResult.structure shouldBe expectedFunctionTraversalResult.structure
  }

  test("traverse with one param and a block when shouldBodyReturnValue='Uncertain'") {
    val param = param"arg: Int"
    val traversedParam = param"traversedArg: Int"

    val body =
      q"""
      doSomething(arg)
      3
      """
      val traversedBody =
      q"""
      doSomethingg(traversedArg)
      /* return? */33
      """

    val function = Term.Function(List(param), body)

    val block = traversedBody
    val expectedFunctionTraversalResult = Term.Function(List(traversedParam), traversedBody)

    doReturn(traversedParam).when(termParamTraverser).traverse(eqTree(param), eqTo(LambdaStatContext))
    doReturn(block)
      .when(defaultBlockTraverser).traverse(eqTree(body), eqBlockContext(BlockContext(shouldReturnValue = Uncertain)))

    val actualResult = termFunctionTraverser.traverse(function, shouldBodyReturnValue = Uncertain)
    actualResult.structure shouldBe expectedFunctionTraversalResult.structure
  }
}
