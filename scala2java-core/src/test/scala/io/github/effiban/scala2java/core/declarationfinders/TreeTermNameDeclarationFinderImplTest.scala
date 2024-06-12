package io.github.effiban.scala2java.core.declarationfinders

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

class TreeTermNameDeclarationFinderImplTest extends UnitTestSuite {

  private val termParamTermNameDeclarationFinder = mock[TermParamTermNameDeclarationFinder]

  private val treeTermNameDeclarationFinder = new TreeTermNameDeclarationFinderImpl(termParamTermNameDeclarationFinder)

  test("find() for Term.Param when found") {
    val termParam = param"x: Int"
    val termName = q"x"

    when(termParamTermNameDeclarationFinder.find(eqTree(termParam), eqTree(termName))).thenReturn(Some(termParam))

    treeTermNameDeclarationFinder.find(termParam, termName).value.structure shouldBe termParam.structure
  }

  test("find() for Term.Param when not found") {
    val termParam = param"x: Int"
    val termName = q"y"

    when(termParamTermNameDeclarationFinder.find(eqTree(termParam), eqTree(termName))).thenReturn(None)

    treeTermNameDeclarationFinder.find(termParam, termName) shouldBe None
  }
}
