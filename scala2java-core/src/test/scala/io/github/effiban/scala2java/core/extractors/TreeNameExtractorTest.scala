package io.github.effiban.scala2java.core.extractors

import io.github.effiban.scala2java.core.extractors.TreeNameExtractor.{extract, extractIndeterminate}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Name, Type, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TreeNameExtractorTest extends UnitTestSuite {

  test("extract for Type.Name") {
    extract(t"X").structure shouldBe t"X".structure
  }

  test("extract for Type.Select") {
    extract(t"a.b.C").structure shouldBe t"C".structure
  }

  test("extract for Type.Project") {
    extract(t"a.B#C").structure shouldBe t"C".structure
  }

  test("extract for Type.Apply") {
    extract(t"a.B[Int]").structure shouldBe t"B".structure
  }

  test("extract for Type.Singleton should return Name.Anonymous") {
    extract(Type.Singleton(q"a.B")).structure shouldBe Name.Anonymous().structure
  }

  test("extract for Term.Name") {
    extract(q"X").structure shouldBe q"X".structure
  }

  test("extract for Term.Select") {
    extract(q"a.b.C").structure shouldBe q"C".structure
  }

  test("extract for Term.Apply") {
    extract(q"a.b(1, 2)").structure shouldBe q"b".structure
  }

  test("extract for Term.ApplyType") {
    extract(q"a.B[Int]").structure shouldBe q"B".structure
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

  test("extract for Template should return Name.Anonymous") {
    extract(template"A with B").structure shouldBe Name.Anonymous().structure
  }

  test("extractIndeterminate for Type.Name") {
    extractIndeterminate(t"X").structure shouldBe Name.Indeterminate("X").structure
  }

  test("extractIndeterminate for Type.Select") {
    extractIndeterminate(t"a.b.C").structure shouldBe Name.Indeterminate("C").structure
  }

  test("extractIndeterminate for Term.Name") {
    extractIndeterminate(q"X").structure shouldBe Name.Indeterminate("X").structure
  }

  test("extractIndeterminate for Term.Select") {
    extractIndeterminate(q"a.b.C").structure shouldBe Name.Indeterminate("C").structure
  }
}
