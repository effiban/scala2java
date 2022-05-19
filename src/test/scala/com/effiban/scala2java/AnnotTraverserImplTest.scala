package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubInitTraverser

import scala.meta.Mod.Annot
import scala.meta.Term.Assign
import scala.meta.{Init, Lit, Name, Term, Type}

class AnnotTraverserImplTest extends UnitTestSuite {

  private val annotTraverser = new AnnotTraverserImpl(new StubInitTraverser)

  test("traverse") {
    annotTraverser.traverse(
      Annot(
        Init(tpe = Type.Name("MyAnnot1"),
          name = Name.Anonymous(),
          argss = List(List(
            Assign(Term.Name("arg1"), Lit.String("val1")),
            Assign(Term.Name("arg2"), Lit.String("val2"))))
        )
      )
    )

    outputWriter.toString shouldBe "@MyAnnot1(arg1 = \"val1\", arg2 = \"val2\")"
  }
}
