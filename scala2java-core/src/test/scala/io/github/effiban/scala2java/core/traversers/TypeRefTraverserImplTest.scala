package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeNameRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type, XtensionQuasiquoteType}

class TypeRefTraverserImplTest extends UnitTestSuite {

  private val typeNameTraverser = mock[TypeNameTraverser]
  private val typeNameRenderer = mock[TypeNameRenderer]
  private val typeSelectTraverser = mock[TypeSelectTraverser]
  private val typeProjectTraverser = mock[TypeProjectTraverser]
  private val typeSingletonTraverser = mock[TypeSingletonTraverser]

  private val typeRefTraverser = new TypeRefTraverserImpl(
    typeNameTraverser,
    typeNameRenderer,
    typeSelectTraverser,
    typeProjectTraverser,
    typeSingletonTraverser
  )

  test("traverse Type.Name") {
    val typeName = t"MyType"
    val traversedTypeName = t"MyTraversedType"

    doReturn(traversedTypeName).when(typeNameTraverser).traverse(eqTree(typeName))

    typeRefTraverser.traverse(typeName)

    verify(typeNameRenderer).render(eqTree(traversedTypeName))
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
