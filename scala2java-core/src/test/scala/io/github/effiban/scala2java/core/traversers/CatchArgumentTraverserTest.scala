package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.CatchArgumentTraverser.traverse

import scala.meta.{Pat, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteType}

class CatchArgumentTraverserTest extends UnitTestSuite {

  test("traverse() for a Pat.Var arg") {
    traverse(p"e").structure shouldBe p"e: Throwable".structure
  }

  test("traverse() for a Pat.Wildcard arg") {
    traverse(Pat.Wildcard()).structure shouldBe Pat.Typed(p"__", t"Throwable").structure
  }

  test("traverse() for a Pat.Typed arg with a wildcard") {
    traverse(Pat.Typed(Pat.Wildcard(), t"MyException")).structure shouldBe Pat.Typed(p"__", t"MyException").structure
  }

  test("traverse() for a Pat.Bind arg") {
    val patBind = p"x@List()"
    traverse(patBind).structure shouldBe patBind.structure
  }
}
