package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type, XtensionQuasiquoteType}

class TypeRefTraverserImplTest extends UnitTestSuite {

  private val typeNameTraverser = mock[TypeNameTraverser]
  private val typeSelectTraverser = mock[TypeSelectTraverser]
  private val typeSingletonTraverser = mock[TypeSingletonTraverser]

  private val typeRefTraverser = new TypeRefTraverserImpl(
    typeNameTraverser,
    typeSelectTraverser,
    typeSingletonTraverser
  )

  test("traverse Type.Name") {
    val typeName = t"MyType"
    val traversedTypeName = t"MyTraversedType"

    doReturn(traversedTypeName).when(typeNameTraverser).traverse(eqTree(typeName))

    typeRefTraverser.traverse(typeName).structure shouldBe traversedTypeName.structure
  }

  test("traverse Type.Select") {
    val typeSelect = t"myObj.MyType"
    val traversedTypeSelect = t"myTraversedObj.MyType"

    doReturn(traversedTypeSelect).when(typeSelectTraverser).traverse(eqTree(typeSelect))

    typeRefTraverser.traverse(typeSelect).structure shouldBe traversedTypeSelect.structure
  }

  test("traverse Type.Project") {
    val typeProject = Type.Project(Type.Name("MyType"), Type.Name("MyInnerType"))

    typeRefTraverser.traverse(typeProject).structure shouldBe typeProject.structure
  }

  test("traverse Type.Singleton") {
    val typeSingleton = Type.Singleton(Term.Name("myObj"))
    val traversedTypeSingleton = Type.Singleton(Term.Name("myTraversedObj"))

    doAnswer(traversedTypeSingleton).when(typeSingletonTraverser).traverse(eqTree(typeSingleton))

    typeRefTraverser.traverse(typeSingleton).structure shouldBe traversedTypeSingleton.structure
  }
}
