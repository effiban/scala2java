package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.unqualifiers.TypeSelectUnqualifier.unqualify

import scala.meta.XtensionQuasiquoteType

class TypeSelectUnqualifierTest extends UnitTestSuite {

  test("unqualify") {
    unqualify(t"a.b.C").structure shouldBe t"C".structure
  }
}
