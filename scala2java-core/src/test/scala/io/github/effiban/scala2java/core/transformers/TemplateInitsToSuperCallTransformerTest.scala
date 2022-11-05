package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Init, Lit, Name, Term, Type}

class TemplateInitsToSuperCallTransformerTest extends UnitTestSuite {

  private val TermNameSuper = Term.Name("super")

  test("transform when no inits should return None") {
    TemplateInitsToSuperCallTransformer.transform(Nil) shouldBe None
  }

  test("transform when one init with one list of args should return with those args") {
    val inputArgs = List(Lit.Int(3), Lit.Int(4))
    val init =
      Init(
        tpe = Type.Name("MySuperClass"),
        name = Name.Anonymous(),
        argss = List(inputArgs)
      )

    val expectedSuperCall = Term.Apply(fun = TermNameSuper, args = inputArgs)

    TemplateInitsToSuperCallTransformer.transform(List(init)).value.structure shouldBe expectedSuperCall.structure
  }

  test("transform when one init with two lists of args should return with concatenated args") {
    val inputArgs1 = List(Lit.Int(3), Lit.Int(4))
    val inputArgs2 = List(Lit.Int(5), Lit.Int(6))

    val init =
      Init(
        tpe = Type.Name("MySuperClass"),
        name = Name.Anonymous(),
        argss = List(inputArgs1, inputArgs2)
      )

    val expectedSuperCall = Term.Apply(fun = TermNameSuper, args = inputArgs1 ++ inputArgs2)

    TemplateInitsToSuperCallTransformer.transform(List(init)).value.structure shouldBe expectedSuperCall.structure
  }

  test("transform when two inits and only second has args should return with args of second") {
    val inputArgs2 = List(Lit.Int(3), Lit.Int(4))

    val init1 =
      Init(
        tpe = Type.Name("MySuperClass1"),
        name = Name.Anonymous(),
        argss = Nil
      )

    val init2 =
      Init(
        tpe = Type.Name("MySuperClass2"),
        name = Name.Anonymous(),
        argss = List(inputArgs2)
      )

    val expectedSuperCall = Term.Apply(fun = TermNameSuper, args = inputArgs2)

    TemplateInitsToSuperCallTransformer.transform(List(init1, init2)).value.structure shouldBe expectedSuperCall.structure
  }

  test("transform when two inits and both have args should return with args of first") {
    val inputArgs1 = List(Lit.Int(3), Lit.Int(4))
    val inputArgs2 = List(Lit.Int(5), Lit.Int(6))

    val init1 =
      Init(
        tpe = Type.Name("MySuperClass1"),
        name = Name.Anonymous(),
        argss = List(inputArgs1)
      )

    val init2 =
      Init(
        tpe = Type.Name("MySuperClass2"),
        name = Name.Anonymous(),
        argss = List(inputArgs2)
      )

    val expectedSuperCall = Term.Apply(fun = TermNameSuper, args = inputArgs1)

    TemplateInitsToSuperCallTransformer.transform(List(init1, init2)).value.structure shouldBe expectedSuperCall.structure
  }
}
