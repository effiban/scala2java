package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type, XtensionQuasiquoteType}

class TypeRefRendererImplTest extends UnitTestSuite {

  private val typeNameRenderer = mock[TypeNameRenderer]
  private val typeSelectRenderer = mock[TypeSelectRenderer]
  private val typeSingletonRenderer = mock[TypeSingletonRenderer]

  private val typeRefRenderer = new TypeRefRendererImpl(
    typeNameRenderer,
    typeSelectRenderer,
    typeSingletonRenderer
  )

  test("render Type.Name") {
    val typeName = t"MyType"

    typeRefRenderer.render(typeName)

    verify(typeNameRenderer).render(eqTree(typeName))
  }

  test("render Type.Select") {
    val typeSelect = t"myPkg.MyType"

    typeRefRenderer.render(typeSelect)

    verify(typeSelectRenderer).render(eqTree(typeSelect))
  }

  test("render Type.Singleton") {
    val typeSingleton = Type.Singleton(Term.Name("myObj"))

    typeRefRenderer.render(typeSingleton)

    verify(typeSingletonRenderer).render(eqTree(typeSingleton))
  }
}
