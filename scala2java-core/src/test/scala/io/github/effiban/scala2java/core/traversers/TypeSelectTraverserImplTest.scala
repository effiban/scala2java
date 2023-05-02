package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{DefaultTermRefRenderer, TypeNameRenderer}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TypeSelectTraverserImplTest extends UnitTestSuite {

  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]
  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]
  private val typeNameTraverser = mock[TypeNameTraverser]
  private val typeNameRenderer = mock[TypeNameRenderer]

  private val typeSelectTraverser = new TypeSelectTraverserImpl(
    defaultTermRefTraverser,
    defaultTermRefRenderer,
    typeNameTraverser,
    typeNameRenderer
  )

  test("traverse()") {
    val qual = q"myObj"
    val traversedQual = q"myTraversedObj"
    val tpe = t"MyType"
    val traversedType = t"MyTraversedType"

    val typeSelect = Type.Select(qual, tpe)

    doReturn(traversedQual).when(defaultTermRefTraverser).traverse(eqTree(qual))
    doWrite("myTraversedObj").when(defaultTermRefRenderer).render(eqTree(traversedQual))
    doReturn(traversedType).when(typeNameTraverser).traverse(eqTree(tpe))
    doWrite("MyTraversedType").when(typeNameRenderer).render(eqTree(traversedType))

    typeSelectTraverser.traverse(typeSelect)

    outputWriter.toString shouldBe "myTraversedObj.MyTraversedType"
  }
}
