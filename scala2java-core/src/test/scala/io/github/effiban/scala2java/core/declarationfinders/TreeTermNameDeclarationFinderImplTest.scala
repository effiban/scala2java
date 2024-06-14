package io.github.effiban.scala2java.core.declarationfinders

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Term, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

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

  test("find() for Decl.Def with one param list when one of the params matches") {
    val declDef = q"def foo(x: scala.Int, y: scala.Int): scala.Int"
    val termName = q"y"
    val patVar = p"y"

    doAnswer((termParam: Term.Param) => termParam match {
      case param"y: scala.Int" => Some(patVar)
      case _ => None
    }).when(termParamTermNameDeclarationFinder).find(any(), eqTree(termName))

    treeTermNameDeclarationFinder.find(declDef, termName).value.structure shouldBe patVar.structure
  }

  test("find() for Decl.Def with two param lists when one of the params matches") {
    val declDef = q"def foo(x: scala.Int, y: scala.Int)(z: scala.Int): scala.Int"
    val termName = q"z"
    val patVar = p"z"

    doAnswer((termParam: Term.Param) => termParam match {
      case param"z: scala.Int" => Some(patVar)
      case _ => None
    }).when(termParamTermNameDeclarationFinder).find(any(), eqTree(termName))

    treeTermNameDeclarationFinder.find(declDef, termName).value.structure shouldBe patVar.structure
  }

  test("find() for Decl.Def with two param lists when none of the params matches") {
    val declDef = q"def foo(x: scala.Int, y: scala.Int)(z: scala.Int): scala.Int"
    val termName = q"w"

    when(termParamTermNameDeclarationFinder.find(any(), eqTree(termName))).thenReturn(None)

    treeTermNameDeclarationFinder.find(declDef, termName) shouldBe None
  }

  test("find() for Defn.Def with one param list when one of the params matches") {
    val defnDef = q"def foo(x: scala.Int, y: scala.Int): scala.Int = 3"
    val termName = q"y"
    val patVar = p"y"

    doAnswer((termParam: Term.Param) => termParam match {
      case param"y: scala.Int" => Some(patVar)
      case _ => None
    }).when(termParamTermNameDeclarationFinder).find(any(), eqTree(termName))

    treeTermNameDeclarationFinder.find(defnDef, termName).value.structure shouldBe patVar.structure
  }

  test("find() for Defn.Def with two param lists when one of the params matches") {
    val defnDef = q"def foo(x: scala.Int, y: scala.Int)(z: scala.Int): scala.Int = 3"
    val termName = q"z"
    val patVar = p"z"

    doAnswer((termParam: Term.Param) => termParam match {
      case param"z: scala.Int" => Some(patVar)
      case _ => None
    }).when(termParamTermNameDeclarationFinder).find(any(), eqTree(termName))

    treeTermNameDeclarationFinder.find(defnDef, termName).value.structure shouldBe patVar.structure
  }

  test("find() for Defn.Def with two param lists when none of the params matches") {
    val defnDef = q"def foo(x: scala.Int, y: scala.Int)(z: scala.Int): scala.Int = 3"
    val termName = q"w"

    when(termParamTermNameDeclarationFinder.find(any(), eqTree(termName))).thenReturn(None)

    treeTermNameDeclarationFinder.find(defnDef, termName) shouldBe None
  }

  test("find() for Term.Function when one of the params matches") {
    val function = q"(x: scala.Int, y: scala.Int) => x + y"
    val termName = q"y"
    val patVar = p"y"

    doAnswer((termParam: Term.Param) => termParam match {
      case param"y: scala.Int" => Some(patVar)
      case _ => None
    }).when(termParamTermNameDeclarationFinder).find(any(), eqTree(termName))

    treeTermNameDeclarationFinder.find(function, termName).value.structure shouldBe patVar.structure
  }

  test("find() for Term.Function when none of the params match") {
    val function = q"(x: scala.Int, y: scala.Int) => x + y"
    val termName = q"z"

    when(termParamTermNameDeclarationFinder.find(any(), eqTree(termName))).thenReturn(None)

    treeTermNameDeclarationFinder.find(function, termName) shouldBe None
  }

  test("find() for Defn.Class with one param list when one of the params matches") {
    val defnClass = q"class A(x: scala.Int, y: scala.Int)"
    val termName = q"y"
    val patVar = p"y"

    doAnswer((termParam: Term.Param) => termParam match {
      case param"y: scala.Int" => Some(patVar)
      case _ => None
    }).when(termParamTermNameDeclarationFinder).find(any(), eqTree(termName))

    treeTermNameDeclarationFinder.find(defnClass, termName).value.structure shouldBe patVar.structure
  }

  test("find() for Defn.Class with two param lists when one of the params matches") {
    val defnClass = q"class A(x: scala.Int, y: scala.Int)(z: scala.Int)"
    val termName = q"z"
    val patVar = p"z"

    doAnswer((termParam: Term.Param) => termParam match {
      case param"z: scala.Int" => Some(patVar)
      case _ => None
    }).when(termParamTermNameDeclarationFinder).find(any(), eqTree(termName))

    treeTermNameDeclarationFinder.find(defnClass, termName).value.structure shouldBe patVar.structure
  }

  test("find() for Defn.Class with two param lists when none of the params matches") {
    val defnClass = q"class A(x: scala.Int, y: scala.Int)(z: scala.Int)"
    val termName = q"w"

    when(termParamTermNameDeclarationFinder.find(any(), eqTree(termName))).thenReturn(None)

    treeTermNameDeclarationFinder.find(defnClass, termName) shouldBe None
  }

}
