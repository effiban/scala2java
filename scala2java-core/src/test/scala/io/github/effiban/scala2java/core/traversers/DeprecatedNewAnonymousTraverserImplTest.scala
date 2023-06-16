package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.NewAnonymous
import scala.meta.{Init, Name, Self, Template, Term, Type}

class DeprecatedNewAnonymousTraverserImplTest extends UnitTestSuite {

  private val templateTraverser = mock[TemplateTraverser]

  private val newAnonymousTraverser = new DeprecatedNewAnonymousTraverserImpl(templateTraverser)

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

    doWrite("/* TEMPLATE BODY */")
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(TemplateContext(javaScope = JavaScope.Class)))

    newAnonymousTraverser.traverse(newAnonymous)

    outputWriter.toString shouldBe "new /* TEMPLATE BODY */"
  }
}