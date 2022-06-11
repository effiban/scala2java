package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite

import scala.meta.Mod.Annot
import scala.meta.Term.Assign
import scala.meta.{Init, Lit, Name, Term, Type}

class AnnotTraverserImplTest extends UnitTestSuite {

  private val initTraverser = mock[InitTraverser]

  private val annotTraverser = new AnnotTraverserImpl(initTraverser)

  test("traverse") {
    val init = Init(tpe = Type.Name("MyAnnot1"),
      name = Name.Anonymous(),
      argss = List(List(
        Assign(Term.Name("arg1"), Lit.String("val1")),
        Assign(Term.Name("arg2"), Lit.String("val2"))))
    )

    doWrite("""MyAnnot1(arg1 = "val1", arg2 = "val2")""").when(initTraverser).traverse(eqTree(init))

    annotTraverser.traverse(Annot(init))

    outputWriter.toString shouldBe """@MyAnnot1(arg1 = "val1", arg2 = "val2")"""
  }
}
