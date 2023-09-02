package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Pat, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteType}

class CatchArgumentTraverserImplTest extends UnitTestSuite {

  private val patTraverser = mock[PatTraverser]

  private val catchArgumentTraverser = new CatchArgumentTraverserImpl(patTraverser)

  test("traverse() for a Pat.Var arg") {
    val pat = p"e"
    val transformedPat = p"e: scala.Throwable"
    val traversedPat = p"e: Throwable"
    doReturn(traversedPat).when(patTraverser).traverse(eqTree(transformedPat))
    catchArgumentTraverser.traverse(pat).structure shouldBe traversedPat.structure
  }

  test("traverse() for a Pat.Wildcard arg") {
    val pat = Pat.Wildcard()
    val transformedPat = Pat.Typed(p"__", t"scala.Throwable")
    val traversedPat = Pat.Typed(p"__", t"Throwable")
    doReturn(traversedPat).when(patTraverser).traverse(eqTree(transformedPat))
    catchArgumentTraverser.traverse(pat).structure shouldBe traversedPat.structure
  }

  test("traverse() for a Pat.Typed arg with a wildcard") {
    val pat = Pat.Typed(Pat.Wildcard(), t"MyException")
    val transformedPat = Pat.Typed(p"__", t"MyException")
    val traversedPat = Pat.Typed(p"__", t"MyTraversedException")
    doReturn(traversedPat).when(patTraverser).traverse(eqTree(transformedPat))
    catchArgumentTraverser.traverse(pat).structure shouldBe traversedPat.structure
  }

  test("traverse() for a Pat.Bind arg") {
    val pat = p"x@List()"
    val traversedPat = p"xx@List()"
    doReturn(traversedPat).when(patTraverser).traverse(eqTree(pat))
    catchArgumentTraverser.traverse(pat).structure shouldBe traversedPat.structure
  }
}
