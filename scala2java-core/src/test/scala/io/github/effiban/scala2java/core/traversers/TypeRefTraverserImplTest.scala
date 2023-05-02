package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{TypeNameRenderer, TypeSelectRenderer}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TypeRefTraverserImplTest extends UnitTestSuite {

  private val typeNameTraverser = mock[TypeNameTraverser]
  private val typeNameRenderer = mock[TypeNameRenderer]
  private val typeSelectTraverser = mock[TypeSelectTraverser]
  private val typeSelectRenderer = mock[TypeSelectRenderer]
  private val typeProjectTraverser = mock[TypeProjectTraverser]
  private val typeSingletonTraverser = mock[TypeSingletonTraverser]

  private val typeRefTraverser = new TypeRefTraverserImpl(
    typeNameTraverser,
    typeNameRenderer,
    typeSelectTraverser,
    typeSelectRenderer,
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
    val typeSelect = t"myObj.MyType"
    val traversedTypeSelect = t"myTraversedObj.MyType"

    doReturn(traversedTypeSelect).when(typeSelectTraverser).traverse(eqTree(typeSelect))

    typeRefTraverser.traverse(typeSelect)

    verify(typeSelectRenderer).render(eqTree(traversedTypeSelect))
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
