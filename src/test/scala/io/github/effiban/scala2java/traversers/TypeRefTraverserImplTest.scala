package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Term, Type}

class TypeRefTraverserImplTest extends UnitTestSuite {

  private val typeNameTraverser = mock[TypeNameTraverser]
  private val typeSelectTraverser = mock[TypeSelectTraverser]
  private val typeProjectTraverser = mock[TypeProjectTraverser]
  private val typeSingletonTraverser = mock[TypeSingletonTraverser]

  private val typeRefTraverser = new TypeRefTraverserImpl(
    typeNameTraverser,
    typeSelectTraverser,
    typeProjectTraverser,
    typeSingletonTraverser
  )

  test("traverse Type.Name") {
    val typeName = Type.Name("MyType")

    typeRefTraverser.traverse(typeName)

    verify(typeNameTraverser).traverse(eqTree(typeName))
  }

  test("traverse Type.Select") {
    val typeSelect = Type.Select(Term.Name("myObj"), Type.Name("MyType"))

    typeRefTraverser.traverse(typeSelect)

    verify(typeSelectTraverser).traverse(eqTree(typeSelect))
  }

  test("traverse Type.Project") {
    val typeProject = Type.Project(Type.Name("MyType"), Type.Name("MyInnerType"))

    typeRefTraverser.traverse(typeProject)

    verify(typeProjectTraverser).traverse(eqTree(typeProject))
  }

  test("traverse Type.Singleton") {
    val typeSingleton = Type.Singleton(Term.Name("myObj"))

    typeRefTraverser.traverse(typeSingleton)

    verify(typeSingletonTraverser).traverse(eqTree(typeSingleton))
  }
}
