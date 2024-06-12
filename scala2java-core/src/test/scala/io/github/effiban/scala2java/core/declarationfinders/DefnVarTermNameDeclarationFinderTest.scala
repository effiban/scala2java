package io.github.effiban.scala2java.core.declarationfinders

import io.github.effiban.scala2java.core.declarationfinders.DefnVarTermNameDeclarationFinder.find
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm}

class DefnVarTermNameDeclarationFinderTest extends UnitTestSuite {

  test("find for single var when exists") {
    val defnVar = q"var x: scala.Int = 3"
    val termName = q"x"
    val patVar = p"x"

    find(defnVar, termName).value.structure shouldBe patVar.structure
  }

  test("find for single var when doesnt exist") {
    val defnVar = q"var x: scala.Int = 3"
    val termName = q"y"

    find(defnVar, termName) shouldBe None
  }

  test("find for pattern tuple when exists in tuple") {
    val defnVar = q"var (x, y): (scala.Int, scala.Int) = (3, 4)"
    val termName = q"y"
    val patVar = p"y"

    find(defnVar, termName).value.structure shouldBe patVar.structure
  }

  test("find for pattern tuple when doesn't exist in tuple") {
    val defnVar = q"var (x, y): (scala.Int, scala.Int) = (3, 4)"
    val termName = q"z"

    find(defnVar, termName) shouldBe None
  }

  test("find for multiple vars when included in them") {
    val defnVar = q"var x, y: scala.Int = 3"
    val termName = q"y"
    val patVar = p"y"

    find(defnVar, termName).value.structure shouldBe patVar.structure
  }

  test("find for multiple vars when not included in them") {
    val defnVar = q"var x, y: scala.Int = 3"
    val termName = q"z"

    find(defnVar, termName) shouldBe None
  }
}
