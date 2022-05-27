package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubInitTraverser

import scala.meta.Term.New
import scala.meta.{Init, Name, Term, Type}

class NewTraverserImplTest extends UnitTestSuite {

  private val newTraverser = new NewTraverserImpl(new StubInitTraverser())

  test("traverse") {
    val `new` = New(
      Init(
        tpe = Type.Name("MyClass"),
        name = Name.Anonymous(),
        argss = List(List(Term.Name("val1"), Term.Name("val2")))
      )
    )

    newTraverser.traverse(`new`)

    outputWriter.toString shouldBe "new MyClass(val1, val2)"
  }
}
