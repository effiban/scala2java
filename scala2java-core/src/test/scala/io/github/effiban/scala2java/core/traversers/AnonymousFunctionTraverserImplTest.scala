package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.AnonymousFunction
import scala.meta.XtensionQuasiquoteTerm

class AnonymousFunctionTraverserImplTest extends UnitTestSuite {

  private val termFunctionTraverser = mock[TermFunctionTraverser]

  private val anonymousFunctionTraverser = new AnonymousFunctionTraverserImpl(termFunctionTraverser)

  test("traverse() when body is a block") {
    val body =
      q"""
      doA()
      doB()
      """

    val expectedFunction =
      q"""
      __ => {
        doA()
        doB()
      }
      """

    val expectedResult = expectedFunction

    doReturn(expectedResult).when(termFunctionTraverser).traverse(eqTree(expectedFunction), eqTo(No))

    anonymousFunctionTraverser.traverse(AnonymousFunction(body)).structure shouldBe expectedFunction.structure
  }

  test("traverse() when body is a block and shouldBodyReturnValue=Yes") {
    val body =
      q"""
      doA()
      doB()
      """

    val expectedFunction =
      q"""
      __ => {
        doA()
        doB()
      }
      """

    val expectedResult = expectedFunction

    doReturn(expectedResult).when(termFunctionTraverser).traverse(eqTree(expectedFunction), shouldBodyReturnValue = eqTo(Yes))

    val actualResult = anonymousFunctionTraverser.traverse(AnonymousFunction(body), shouldBodyReturnValue = Yes)
    actualResult.structure shouldBe expectedResult.structure
  }

  test("traverse() when body is a block, shouldBodyReturnValue=Uncertain and output uncertainReturn=true") {
    val body =
      q"""
      doA()
      doB()
      """

    val expectedFunction =
      q"""
      __ => {
        doA()
        /* return? */doB()
      }
      """

    val expectedResult = expectedFunction

    doReturn(expectedResult).when(termFunctionTraverser).traverse(eqTree(expectedFunction), shouldBodyReturnValue = eqTo(Uncertain))

    val actualResult = anonymousFunctionTraverser.traverse(AnonymousFunction(body), shouldBodyReturnValue = Uncertain)
    actualResult.structure shouldBe expectedResult.structure
  }
}
