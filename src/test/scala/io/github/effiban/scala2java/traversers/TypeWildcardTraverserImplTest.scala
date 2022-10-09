package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Type

class TypeWildcardTraverserImplTest extends UnitTestSuite {

  private val typeBoundsTraverser = mock[TypeBoundsTraverser]

  private val typeAnonymousParamTraverser = new TypeWildcardTraverserImpl(typeBoundsTraverser)

  test("traverse") {
    val bounds = Type.Bounds(lo = None, hi = Some(Type.Name("T")))

    doWrite(" extends T").when(typeBoundsTraverser).traverse(eqTree(bounds))

    typeAnonymousParamTraverser.traverse(Type.Wildcard(bounds))

    outputWriter.toString shouldBe "? extends T"
  }

}
