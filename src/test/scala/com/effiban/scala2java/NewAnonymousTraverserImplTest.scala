package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubTemplateTraverser

import scala.meta.Term.NewAnonymous
import scala.meta.{Init, Name, Self, Template, Term, Type}

class NewAnonymousTraverserImplTest extends UnitTestSuite {

  private val newAnonymousTraverser = new NewAnonymousTraverserImpl(new StubTemplateTraverser())

  test("traverse") {
    val newAnonymous = NewAnonymous(
      Template(
        early = Nil,
        inits = List(
          Init(
            tpe = Type.Name("MyParent"),
            name = Name.Anonymous(),
            argss = List(List(Term.Name("parentArg1"), Term.Name("parentArg2")))
          )
        ),
        self = Self(name = Name.Anonymous(), decltpe = None),
        stats = List(Term.Apply(Term.Name("doSomething"), List(Term.Name("arg")))))
    )

    newAnonymousTraverser.traverse(newAnonymous)

    outputWriter.toString shouldBe
      "new " +
        """
          |/**
          |* STUB TEMPLATE
          |* Input ClassInfo: None
          |* Scala Body:
          |* MyParent(parentArg1, parentArg2) { doSomething(arg) }
          |*/
          |""".stripMargin
  }
}
