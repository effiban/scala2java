package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Type

class TypeWildcardRendererImplTest extends UnitTestSuite {

  private val typeBoundsRenderer = mock[TypeBoundsRenderer]

  private val typeAnonymousParamRenderer = new TypeWildcardRendererImpl(typeBoundsRenderer)

  test("traverse") {
    val bounds = Type.Bounds(lo = None, hi = Some(Type.Name("T")))

    doWrite(" extends T").when(typeBoundsRenderer).render(eqTree(bounds))

    typeAnonymousParamRenderer.render(Type.Wildcard(bounds))

    outputWriter.toString shouldBe "? extends T"
  }

}
