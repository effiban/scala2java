package io.github.effiban.scala2java.core.extractors

import io.github.effiban.scala2java.core.extractors.TypeRefExtractor.extract
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeRefExtractorTest extends UnitTestSuite {

  test("extract when type is a Type.Name") {
    extract(t"A").value.structure shouldBe t"A".structure
  }

  test("extract when type is a Type.Select") {
    extract(t"a.b.C").value.structure shouldBe t"a.b.C".structure
  }

  test("extract when type is a Type.Project") {
    extract(t"a.b.C#D").value.structure shouldBe t"a.b.C#D".structure
  }

  test("extract when type is a Type.Apply of a Type.Name with an argument that is a Type.Name") {
    extract(t"A[B]").value.structure shouldBe t"A".structure
  }

  test("extract when type is a Type.Apply of a Type.Name with an argument that is a Type.Select") {
    extract(t"A[scala.Int]").value.structure shouldBe t"A".structure
  }

  test("extract when type is a Type.Apply of a Type.Select with an argument that is a Type.Name") {
    extract(t"a.B[C]").value.structure shouldBe t"a.B".structure
  }

  test("extract when type is a Type.Apply of a Type.Project with an argument that is a Type.Name") {
    extract(t"a.B#C[D]").value.structure shouldBe t"a.B#C".structure
  }

  test("extract when type is a Type.Repeated of a Type.Name") {
    extract(t"A*").value.structure shouldBe t"A".structure
  }

  test("extract when type is a Type.Repeated of a Type.Select") {
    extract(t"scala.Int*").value.structure shouldBe t"scala.Int".structure
  }

  test("extract when type is a Type.Repeated of a Type.Project") {
    extract(t"a.b#C*").value.structure shouldBe t"a.b#C".structure
  }

  test("extract when type is a Type.Tuple") {
    extract(t"(A, B)") shouldBe None
  }
}
