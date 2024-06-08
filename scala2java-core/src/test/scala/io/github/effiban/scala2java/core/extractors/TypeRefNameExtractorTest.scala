package io.github.effiban.scala2java.core.extractors

import io.github.effiban.scala2java.core.extractors.TypeRefNameExtractor.extract
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Name, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TypeRefNameExtractorTest extends UnitTestSuite {

  test("extract for Type.Name") {
    extract(t"X").structure shouldBe Name.Indeterminate("X").structure
  }

  test("extract for Type.Select") {
    extract(t"a.b.C").structure shouldBe Name.Indeterminate("C").structure
  }

  test("extract for Type.Project") {
    extract(t"a.B#C").structure shouldBe Name.Indeterminate("C").structure
  }

  test("extract for Type.Singleton") {
    extract(Type.Singleton(q"a.B")).structure shouldBe Name.Anonymous().structure
  }
}
