package io.github.effiban.scala2java.core.extractors

import io.github.effiban.scala2java.core.extractors.TreeNameExtractor.extract
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Name, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TreeNameExtractorTest extends UnitTestSuite {

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

  test("extract for Defn.Class") {
    extract(q"class A").structure shouldBe t"A".structure
  }

  test("extract for Defn.Trait") {
    extract(q"trait A").structure shouldBe t"A".structure
  }

  test("extract for Defn.Object") {
    extract(q"object A").structure shouldBe q"A".structure
  }

  test("extract for Decl.Def") {
    extract(q"def a: Int").structure shouldBe q"a".structure
  }

  test("extract for Defn.Def") {
    extract(q"def a: Int = 3").structure shouldBe q"a".structure
  }
}
