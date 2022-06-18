package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite

import scala.meta.Term.NewAnonymous
import scala.meta.{Init, Name, Self, Template, Term, Type}

class NewAnonymousTraverserImplTest extends UnitTestSuite {

  private val templateTraverser = mock[TemplateTraverser]

  private val newAnonymousTraverser = new NewAnonymousTraverserImpl(templateTraverser)

  test("traverse") {

    val template = Template(
      early = Nil,
      inits = List(
        Init(
          tpe = Type.Name("MyParent"),
          name = Name.Anonymous(),
          argss = Nil
        )
      ),
      self = Self(name = Name.Anonymous(), decltpe = None),
      stats = List(Term.Apply(Term.Name("doSomething"), List(Term.Name("arg")))))

    val newAnonymous = NewAnonymous(template)

    doWrite("/* TEMPLATE BODY */").when(templateTraverser).traverse(eqTree(template))

    newAnonymousTraverser.traverse(newAnonymous)

    outputWriter.toString shouldBe "new /* TEMPLATE BODY */"
  }
}
