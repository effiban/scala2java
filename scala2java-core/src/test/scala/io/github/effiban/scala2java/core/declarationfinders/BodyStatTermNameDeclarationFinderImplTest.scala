package io.github.effiban.scala2java.core.declarationfinders

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm}

class BodyStatTermNameDeclarationFinderImplTest extends UnitTestSuite {

  private val declVarTermNameDeclarationFinder = mock[DeclVarTermNameDeclarationFinder]
  private val defnVarTermNameDeclarationFinder = mock[DefnVarTermNameDeclarationFinder]

  private val bodyStatTermNameDeclarationFinder = new BodyStatTermNameDeclarationFinderImpl(
    declVarTermNameDeclarationFinder,
    defnVarTermNameDeclarationFinder
  )

  test("find() when stat is a matching Decl.Var") {
    val declVar = q"var x: scala.Int"
    val termName = q"x"
    val patVar = p"x"

    when(declVarTermNameDeclarationFinder.find(eqTree(declVar), eqTree(termName))).thenReturn(Some(patVar))

    bodyStatTermNameDeclarationFinder.find(declVar, termName).value.structure shouldBe patVar.structure
  }

  test("find() when stat is a non-matching Decl.Var") {
    val declVar = q"var x: scala.Int"
    val termName = q"y"

    when(declVarTermNameDeclarationFinder.find(eqTree(declVar), eqTree(termName))).thenReturn(None)

    bodyStatTermNameDeclarationFinder.find(declVar, termName) shouldBe None
  }

  test("find() when stat is a matching Defn.Var") {
    val defnVar = q"var x: scala.Int = 3"
    val termName = q"x"
    val patVar = p"x"

    when(defnVarTermNameDeclarationFinder.find(eqTree(defnVar), eqTree(termName))).thenReturn(Some(patVar))

    bodyStatTermNameDeclarationFinder.find(defnVar, termName).value.structure shouldBe patVar.structure
  }

  test("find() when stat is a non-matching Defn.Var") {
    val defnVar = q"var x: scala.Int = 3"
    val termName = q"y"

    when(defnVarTermNameDeclarationFinder.find(eqTree(defnVar), eqTree(termName))).thenReturn(None)

    bodyStatTermNameDeclarationFinder.find(defnVar, termName) shouldBe None
  }

  test("find() when stat is a matching Defn.Object") {
    val defnObject = q"object A"
    val termName = q"A"

    bodyStatTermNameDeclarationFinder.find(defnObject, termName).value.structure shouldBe defnObject.structure
  }

  test("find() when stat is a non-matching Defn.Object") {
    val defnObject = q"object A"
    val termName = q"B"

    bodyStatTermNameDeclarationFinder.find(defnObject, termName) shouldBe None
  }

  test("find() when stat is a Term.Apply should return None even if it matches") {
    val termApply = q"foo(3)"
    val termName = q"foo"

    bodyStatTermNameDeclarationFinder.find(termApply, termName) shouldBe None
  }
}
