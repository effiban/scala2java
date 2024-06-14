package io.github.effiban.scala2java.core.declarationfinders

import io.github.effiban.scala2java.core.declarationfinders.TermParamTermNameDeclarationFinder.find
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Decl, Defn, Template, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

class TermParamTermNameDeclarationFinderTest extends UnitTestSuite {

  test("find when matches") {
    val termParam = param"x: Int"
    find(termParam, q"x").value.structure shouldBe termParam.structure
  }

  test("find when doesn't match") {
    val termParam = param"x: Int"
    find(termParam, q"y") shouldBe None
  }
}
