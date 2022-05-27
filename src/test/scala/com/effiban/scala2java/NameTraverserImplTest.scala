package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubTermNameTraverser, StubTypeNameTraverser}
import org.mockito.ArgumentMatchers.any

import scala.meta.{Name, Term, Type}

class NameTraverserImplTest extends UnitTestSuite {

  private val nameIndeterminateTraverser = spy(new StubNameIndeterminateTraverser())
  private val termNameTraverser = spy(new StubTermNameTraverser())
  private val typeNameTraverser = spy(new StubTypeNameTraverser())

  private val nameTraverser = new NameTraverserImpl(StubNameAnonymousTraverser,
    nameIndeterminateTraverser,
    termNameTraverser,
    typeNameTraverser)

  test("traverse for Name.Anonymous") {
    nameTraverser.traverse(Name.Anonymous())
    outputWriter.toString shouldBe ""
  }

  test("traverse for Name.Indeterminate") {
    nameTraverser.traverse(Name.Indeterminate("myName"))
    outputWriter.toString shouldBe "myName"
    verify(nameIndeterminateTraverser).traverse(any())
  }

  test("traverse for Term.Name") {
    nameTraverser.traverse(Term.Name("myTermName"))
    outputWriter.toString shouldBe "myTermName"
    verify(termNameTraverser).traverse(any())
  }

  test("traverse for Term.Type") {
    nameTraverser.traverse(Type.Name("myTypeName"))
    outputWriter.toString shouldBe "myTypeName"
    verify(typeNameTraverser).traverse(any())
  }
}
