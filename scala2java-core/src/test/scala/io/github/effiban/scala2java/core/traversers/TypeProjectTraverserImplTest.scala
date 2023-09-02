package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeProjectTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeProjectTraverser = new TypeProjectTraverserImpl(typeTraverser)

  test("traverse") {
    val qual = t"MyClass"
    val traversedQual = t"MyTraversedClass"
    val name = t"MyInnerClass"
    val typeProject = Type.Project(qual, name)
    val traversedTypeProject = Type.Project(traversedQual, name)

    doReturn(traversedQual).when(typeTraverser).traverse(eqTree(qual))

    typeProjectTraverser.traverse(typeProject).structure shouldBe traversedTypeProject.structure
  }
}
