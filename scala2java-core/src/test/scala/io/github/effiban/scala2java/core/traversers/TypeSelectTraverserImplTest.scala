package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TypeSelectTraverserImplTest extends UnitTestSuite {

  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]
  private val typeSelectTransformer = mock[TypeSelectTransformer]

  private val typeSelectTraverser = new TypeSelectTraverserImpl(
    defaultTermRefTraverser,
    typeSelectTransformer
  )


  test("traverse() when transformed") {
    val typeSelect = t"myPkg.MyType"
    val transformedTypeSelect = t"myTransformedPkg.MyTransformedType"

    doReturn(Some(transformedTypeSelect)).when(typeSelectTransformer).transform(eqTree(typeSelect))

    typeSelectTraverser.traverse(typeSelect).structure shouldBe transformedTypeSelect.structure
  }

  test("traverse() when not transformed") {
    val qual = q"myObj"
    val traversedQual = q"myTraversedObj"

    val typeSelect = t"myObj.MyType"
    val traversedTypeSelect = t"myTraversedObj.MyType"

    doReturn(None).when(typeSelectTransformer).transform(eqTree(typeSelect))
    doReturn(traversedQual).when(defaultTermRefTraverser).traverse(eqTree(qual))

    typeSelectTraverser.traverse(typeSelect).structure shouldBe traversedTypeSelect.structure
  }
}
