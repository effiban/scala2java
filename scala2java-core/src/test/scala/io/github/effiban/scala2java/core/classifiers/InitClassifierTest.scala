package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteInit

class InitClassifierTest extends UnitTestSuite {

  test("isEnum when not 'Enumeration' should return false") {
    InitClassifier.isEnum(init"BlaBla()") shouldBe false
  }

  test("isEnum when 'scala.Enumeration' should return true") {
    InitClassifier.isEnum(init"scala.Enumeration()") shouldBe true
  }
}
