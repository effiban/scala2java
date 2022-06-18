package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree

import scala.meta.{Name, Term, Type}

class NameTraverserImplTest extends UnitTestSuite {

  private val nameIndeterminateTraverser = mock[NameIndeterminateTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val typeNameTraverser = mock[TypeNameTraverser]

  private val nameTraverser = new NameTraverserImpl(StubNameAnonymousTraverser,
    nameIndeterminateTraverser,
    termNameTraverser,
    typeNameTraverser)

  test("traverse for Name.Anonymous") {
    nameTraverser.traverse(Name.Anonymous())

    outputWriter.toString shouldBe ""
  }

  test("traverse for Name.Indeterminate") {
    val name = Name.Indeterminate("myName")

    nameTraverser.traverse(name)

    verify(nameIndeterminateTraverser).traverse(eqTree(name))
  }

  test("traverse for Term.Name") {
    val name = Term.Name("myTermName")

    nameTraverser.traverse(name)

    verify(termNameTraverser).traverse(eqTree(name))
  }

  test("traverse for Term.Type") {
    val name = Type.Name("myTypeName")

    nameTraverser.traverse(name)

    verify(typeNameTraverser).traverse(eqTree(name))
  }
}
