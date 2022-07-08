package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite

import scala.meta.Type

class TypePlaceholderTraverserImplTest extends UnitTestSuite {

  private val typeBoundsTraverser = mock[TypeBoundsTraverser]

  private val typePlaceholderTraverser = new TypePlaceholderTraverserImpl(typeBoundsTraverser)

  test("traverse") {
    val bounds = Type.Bounds(lo = None, hi = Some(Type.Name("T")))

    doWrite(" extends T").when(typeBoundsTraverser).traverse(eqTree(bounds))

    typePlaceholderTraverser.traverse(Type.Placeholder(bounds))

    outputWriter.toString shouldBe "? extends T"
  }

}
