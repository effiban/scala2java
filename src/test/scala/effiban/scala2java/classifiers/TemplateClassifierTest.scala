package effiban.scala2java.classifiers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.Selfs

import scala.meta.{Init, Name, Template, Type}

class TemplateClassifierTest extends UnitTestSuite {

  test("isEnum when no inits should return false") {
    val template = Template(
      early = Nil,
      inits = Nil,
      self = Selfs.Empty,
      stats = Nil
    )

    TemplateClassifier.isEnum(template) shouldBe false
  }

  test("isEnum when has one init that is 'Enumeration' should return true") {
    val template = Template(
      early = Nil,
      inits = List(Init(tpe = Type.Name("Enumeration"), name = Name.Anonymous(), argss = List(Nil))),
      self = Selfs.Empty,
      stats = Nil
    )

    TemplateClassifier.isEnum(template) shouldBe true
  }

  test("isEnum when has one init that is not 'Enumeration' should return false") {
    val template = Template(
      early = Nil,
      inits = List(Init(tpe = Type.Name("blabla"), name = Name.Anonymous(), argss = List(Nil))),
      self = Selfs.Empty,
      stats = Nil
    )

    TemplateClassifier.isEnum(template) shouldBe false
  }

  test("isEnum when has one init that is 'Enumeration' and one that isn't should return true") {
    val template = Template(
      early = Nil,
      inits = List(
        Init(tpe = Type.Name("Enumeration"), name = Name.Anonymous(), argss = List(Nil)),
        Init(tpe = Type.Name("blabla"), name = Name.Anonymous(), argss = List(Nil))
      ),
      self = Selfs.Empty,
      stats = Nil
    )

    TemplateClassifier.isEnum(template) shouldBe true
  }
}
