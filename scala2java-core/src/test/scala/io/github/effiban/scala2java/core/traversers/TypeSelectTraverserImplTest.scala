package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TypeSelectTraverserImplTest extends UnitTestSuite {

  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]

  private val typeSelectTraverser = new TypeSelectTraverserImpl(defaultTermRefTraverser)


  test("traverse() when not transformed") {
    val qual = q"myObj"
    val traversedQual = q"myTraversedObj"

    val typeSelect = t"myObj.MyType"
    val traversedTypeSelect = t"myTraversedObj.MyType"

    doReturn(traversedQual).when(defaultTermRefTraverser).traverse(eqTree(qual))

    typeSelectTraverser.traverse(typeSelect).structure shouldBe traversedTypeSelect.structure
  }
}
