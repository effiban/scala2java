package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeVarRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeVarTraverserImplTest extends UnitTestSuite {

  private val typeVarRenderer = mock[TypeVarRenderer]

  private val typeVarTraverser = new TypeVarTraverserImpl(typeVarRenderer)

  test("traverse") {
    val typeVar = Type.Var(t"x")

    typeVarTraverser.traverse(typeVar)

    verify(typeVarRenderer).render(eqTree(typeVar))
  }

}
