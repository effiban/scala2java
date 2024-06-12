package io.github.effiban.scala2java.core.declarationfinders

import io.github.effiban.scala2java.core.declarationfinders.DeclVarTermNameDeclarationFinder.find
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm}

class DeclVarTermNameDeclarationFinderTest extends UnitTestSuite {

  test("find for simple pattern when exists") {
    val declVar = q"var x: scala.Int"
    val termName = q"x"
    val patVar = p"x"

    find(declVar, termName).value.structure shouldBe patVar.structure
  }

  test("find for simple pattern when doesnt exist") {
    val declVar = q"var x: scala.Int"
    val termName = q"y"

    find(declVar, termName) shouldBe None
  }
}
