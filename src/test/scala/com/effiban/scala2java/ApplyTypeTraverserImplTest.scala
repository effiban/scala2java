package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubTermTraverser, StubTypeListTraverser, StubTypeTraverser}

import scala.meta.{Term, Type}

class ApplyTypeTraverserImplTest extends UnitTestSuite {

  private val applyTypeTraverser = new ApplyTypeTraverserImpl(
    new StubTypeTraverser,
    new StubTermTraverser,
    new StubTypeListTraverser)

  test("traverse() when function is 'classOf' should convert to the Java equivalent") {
    applyTypeTraverser.traverse(Term.ApplyType(fun = Term.Name("classOf"), targs = List(Type.Name("T"))))

    outputWriter.toString shouldBe "T.class"
  }

  test("traverse() when function is regular should handle accordingly") {
    applyTypeTraverser.traverse(Term.ApplyType(fun = Term.Name("myFunc"), targs = List(Type.Name("T1"), Type.Name("T2"))))

    outputWriter.toString shouldBe "myFunc.<T1, T2>"
  }
}
