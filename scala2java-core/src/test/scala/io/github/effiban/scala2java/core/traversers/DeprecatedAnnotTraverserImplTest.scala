package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InitContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.Mod.Annot
import scala.meta.Term.Assign
import scala.meta.{Init, Lit, Name, Term, Type}

class DeprecatedAnnotTraverserImplTest extends UnitTestSuite {

  private val initTraverser = mock[DeprecatedInitTraverser]

  private val annotTraverser = new DeprecatedAnnotTraverserImpl(initTraverser)

  test("traverse") {
    val init = Init(tpe = Type.Name("MyAnnot1"),
      name = Name.Anonymous(),
      argss = List(List(
        Assign(Term.Name("arg1"), Lit.String("val1")),
        Assign(Term.Name("arg2"), Lit.String("val2"))))
    )

    doWrite("""MyAnnot1(arg1 = "val1", arg2 = "val2")""")
      .when(initTraverser).traverse(eqTree(init), ArgumentMatchers.eq(InitContext()))

    annotTraverser.traverse(Annot(init))

    outputWriter.toString shouldBe """@MyAnnot1(arg1 = "val1", arg2 = "val2")"""
  }
}
