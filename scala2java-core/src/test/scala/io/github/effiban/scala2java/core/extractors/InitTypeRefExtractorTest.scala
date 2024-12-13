package io.github.effiban.scala2java.core.extractors

import io.github.effiban.scala2java.core.extractors.InitTypeRefExtractor.extract
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteInit, XtensionQuasiquoteType}

class InitTypeRefExtractorTest extends UnitTestSuite {

  test("extract when type is a Type.Name") {
    extract(init"A").value.structure shouldBe t"A".structure
  }

  test("extract when type is a Type.Select") {
    extract(init"a.b.C").value.structure shouldBe t"a.b.C".structure
  }

  test("extract when type is a Type.Project") {
    extract(init"a.b.C#D").value.structure shouldBe t"a.b.C#D".structure
  }

  test("extract when type is a Type.Apply of a Type.Name") {
    extract(init"A[Int]").value.structure shouldBe t"A".structure
  }

  test("extract when type is a Type.Apply of a Type.Select") {
    extract(init"a.B[Int]").value.structure shouldBe t"a.B".structure
  }

  test("extract when type is a Type.Apply of a Type.Project") {
    extract(init"a.B#C[Int]").value.structure shouldBe t"a.B#C".structure
  }
}
