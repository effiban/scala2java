package effiban.scala2java.traversers

import effiban.scala2java.contexts.TemplateContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.matchers.TemplateContextMatcher.eqTemplateContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

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

    doWrite("/* TEMPLATE BODY */")
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(TemplateContext(javaScope = JavaTreeType.Class)))

    newAnonymousTraverser.traverse(newAnonymous)

    outputWriter.toString shouldBe "new /* TEMPLATE BODY */"
  }
}
