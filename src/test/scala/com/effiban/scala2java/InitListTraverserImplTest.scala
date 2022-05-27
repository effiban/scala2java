package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubArgumentListTraverser, StubInitTraverser}

import scala.meta.{Init, Name, Term, Type}

class InitListTraverserImplTest extends UnitTestSuite {

  private val initListTraverser = new InitListTraverserImpl(new StubArgumentListTraverser(), new StubInitTraverser())


  test("traverse() when no inits") {
    initListTraverser.traverse(Nil)

    outputWriter.toString shouldBe ""
  }

  test("traverse() when one init") {

    val init = Init(tpe = Type.Name("MyType"), name = Name.Anonymous(), argss = List(List(Term.Name("arg1"), Term.Name("arg2"))))

    initListTraverser.traverse(List(init))

    outputWriter.toString shouldBe "MyType(arg1, arg2)"
  }

  test("traverse() when two inits") {
    val init1 = Init(tpe = Type.Name("MyType1"), name = Name.Anonymous(), argss = List(List(Term.Name("arg1"), Term.Name("arg2"))))
    val init2 = Init(tpe = Type.Name("MyType2"), name = Name.Anonymous(), argss = List(List(Term.Name("arg3"), Term.Name("arg4"))))

    initListTraverser.traverse(List(init1, init2))

    outputWriter.toString shouldBe
      """MyType1(arg1, arg2),
      |MyType2(arg3, arg4)""".stripMargin
  }
}
