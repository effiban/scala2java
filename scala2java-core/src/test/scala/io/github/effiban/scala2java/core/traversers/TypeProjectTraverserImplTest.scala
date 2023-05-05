package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeProjectTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val typeNameTraverser = mock[TypeNameTraverser]

  private val typeProjectTraverser = new TypeProjectTraverserImpl(
    typeTraverser,
    typeNameTraverser
  )

  test("traverse") {
    val qual = t"MyClass"
    val traversedQual = t"MyTraversedClass"
    val name = t"MyInnerClass"
    val traversedName = t"MyTraversedInnerClass"
    val typeProject = Type.Project(qual, name)
    val traversedTypeProject = Type.Project(traversedQual, traversedName)

    doReturn(traversedQual).when(typeTraverser).traverse(eqTree(qual))
    doReturn(traversedName).when(typeNameTraverser).traverse(eqTree(name))

    typeProjectTraverser.traverse(typeProject).structure shouldBe traversedTypeProject.structure
  }
}
