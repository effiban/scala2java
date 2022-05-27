package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubTermListTraverser, StubTypeTraverser}

import scala.meta.{Init, Name, Term, Type}

class InitTraverserImplTest extends UnitTestSuite {

  private val initTraverser = new InitTraverserImpl(new StubTypeTraverser(), new StubTermListTraverser())


  test("traverse() with one argument list") {

    val init = Init(tpe = Type.Name("MyType"), name = Name.Anonymous(), argss = List(List(Term.Name("arg1"), Term.Name("arg2"))))

    initTraverser.traverse(init)

    outputWriter.toString shouldBe
      """MyType(arg1,
      |arg2)""".stripMargin
  }

  test("traverse() with two argument lists should concat them") {

    val init = Init(
      tpe = Type.Name("MyType"),
      name = Name.Anonymous(),
      argss = List(
        List(Term.Name("arg1"), Term.Name("arg2")),
        List(Term.Name("arg3"), Term.Name("arg4"))
      )
    )

    initTraverser.traverse(init)

    outputWriter.toString shouldBe
      """MyType(arg1,
        |arg2,
        |arg3,
        |arg4)""".stripMargin
  }
}
