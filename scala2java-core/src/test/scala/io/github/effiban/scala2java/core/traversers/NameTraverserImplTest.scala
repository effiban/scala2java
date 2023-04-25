package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Name, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class NameTraverserImplTest extends UnitTestSuite {

  private val typeNameTraverser = mock[TypeNameTraverser]

  private val nameTraverser = new NameTraverserImpl(typeNameTraverser)

  test("traverse for Name.Anonymous") {
    nameTraverser.traverse(Name.Anonymous()).structure shouldBe Name.Anonymous().structure
  }

  test("traverse for Name.Indeterminate") {
    val nameIndeterminate = Name.Indeterminate("myName")

    nameTraverser.traverse(nameIndeterminate).structure shouldBe nameIndeterminate.structure
  }

  test("traverse for Term.Name") {
    val name = q"myTermName"

    nameTraverser.traverse(name).structure shouldBe name.structure
  }

  test("traverse for Type.Name") {
    val name = t"myTypeName"
    val traversedName = t"myTraversedTypeName"

    doReturn(traversedName).when(typeNameTraverser).traverse(name)

    nameTraverser.traverse(name).structure shouldBe traversedName.structure
  }
}
