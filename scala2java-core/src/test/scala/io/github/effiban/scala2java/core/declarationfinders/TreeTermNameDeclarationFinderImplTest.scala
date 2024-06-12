package io.github.effiban.scala2java.core.declarationfinders

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

class TreeTermNameDeclarationFinderImplTest extends UnitTestSuite {

  private val termParamTermNameDeclarationFinder = mock[TermParamTermNameDeclarationFinder]
  private val declVarTermNameDeclarationFinder = mock[DeclVarTermNameDeclarationFinder]
  private val defnVarTermNameDeclarationFinder = mock[DefnVarTermNameDeclarationFinder]

  private val treeTermNameDeclarationFinder = new TreeTermNameDeclarationFinderImpl(
    termParamTermNameDeclarationFinder,
    declVarTermNameDeclarationFinder,
    defnVarTermNameDeclarationFinder
  )

  test("find() for Term.Param when found") {
    val termParam = param"x: scala.Int"
    val termName = q"x"

    when(termParamTermNameDeclarationFinder.find(eqTree(termParam), eqTree(termName))).thenReturn(Some(termParam))

    treeTermNameDeclarationFinder.find(termParam, termName).value.structure shouldBe termParam.structure
  }

  test("find() for Term.Param when not found") {
    val termParam = param"x: scala.Int"
    val termName = q"y"

    when(termParamTermNameDeclarationFinder.find(eqTree(termParam), eqTree(termName))).thenReturn(None)

    treeTermNameDeclarationFinder.find(termParam, termName) shouldBe None
  }

  test("find() for Decl.Var when found") {
    val declVar = q"var x: scala.Int"
    val termName = q"x"
    val patVar = p"x"

    when(declVarTermNameDeclarationFinder.find(eqTree(declVar), eqTree(termName))).thenReturn(Some(patVar))

    treeTermNameDeclarationFinder.find(declVar, termName).value.structure shouldBe patVar.structure
  }

  test("find() for Decl.Var when not found") {
    val declVar = q"var x: scala.Int"
    val termName = q"y"

    when(declVarTermNameDeclarationFinder.find(eqTree(declVar), eqTree(termName))).thenReturn(None)

    treeTermNameDeclarationFinder.find(declVar, termName) shouldBe None
  }

  test("find() for Defn.Var when found") {
    val defnVar = q"var x: scala.Int = 3"
    val termName = q"x"
    val patVar = p"x"

    when(defnVarTermNameDeclarationFinder.find(eqTree(defnVar), eqTree(termName))).thenReturn(Some(patVar))

    treeTermNameDeclarationFinder.find(defnVar, termName).value.structure shouldBe patVar.structure
  }

  test("find() for Defn.Var when not found") {
    val defnVar = q"var x: scala.Int = 3"
    val termName = q"y"

    when(defnVarTermNameDeclarationFinder.find(eqTree(defnVar), eqTree(termName))).thenReturn(None)

    treeTermNameDeclarationFinder.find(defnVar, termName) shouldBe None
  }
}
