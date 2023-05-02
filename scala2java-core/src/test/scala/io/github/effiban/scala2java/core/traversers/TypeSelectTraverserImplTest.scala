package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TypeSelectTraverserImplTest extends UnitTestSuite {

  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]
  private val typeNameTraverser = mock[TypeNameTraverser]

  private val typeSelectTraverser = new TypeSelectTraverserImpl(
    defaultTermRefTraverser,
    typeNameTraverser
  )

  test("traverse()") {
    val qual = q"myObj"
    val traversedQual = q"myTraversedObj"
    val tpe = t"MyType"
    val traversedType = t"MyTraversedType"

    val typeSelect = t"myObj.MyType"
    val traversedTypeSelect = t"myTraversedObj.MyTraversedType"

    doReturn(traversedQual).when(defaultTermRefTraverser).traverse(eqTree(qual))
    doReturn(traversedType).when(typeNameTraverser).traverse(eqTree(tpe))

    typeSelectTraverser.traverse(typeSelect).structure shouldBe traversedTypeSelect.structure
  }
}
