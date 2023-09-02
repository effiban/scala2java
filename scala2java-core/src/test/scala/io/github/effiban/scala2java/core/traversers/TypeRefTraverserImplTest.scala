package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type, XtensionQuasiquoteType}

class TypeRefTraverserImplTest extends UnitTestSuite {

  private val typeSelectTraverser = mock[TypeSelectTraverser]
  private val typeProjectTraverser = mock[TypeProjectTraverser]

  private val typeRefTraverser = new TypeRefTraverserImpl(
    typeSelectTraverser,
    typeProjectTraverser
  )

  test("traverse Type.Name") {
    val typeName = t"MyType"

    typeRefTraverser.traverse(typeName).structure shouldBe typeName.structure
  }

  test("traverse Type.Select") {
    val typeSelect = t"myObj.MyType"
    val traversedTypeSelect = t"myTraversedObj.MyType"

    doReturn(traversedTypeSelect).when(typeSelectTraverser).traverse(eqTree(typeSelect))

    typeRefTraverser.traverse(typeSelect).structure shouldBe traversedTypeSelect.structure
  }

  test("traverse Type.Singleton") {
    val typeSingleton = Type.Singleton(Term.Name("myObj"))

    typeRefTraverser.traverse(typeSingleton).structure shouldBe typeSingleton.structure
  }

  test("traverse Type.Project") {
    val typeProject = t"MyType#MyInnerType"
    val traversedTypeProject = t"MyTraversedType#MyInnerType"

    doAnswer(traversedTypeProject).when(typeProjectTraverser).traverse(eqTree(typeProject))

    typeRefTraverser.traverse(typeProject).structure shouldBe traversedTypeProject.structure
  }
}
