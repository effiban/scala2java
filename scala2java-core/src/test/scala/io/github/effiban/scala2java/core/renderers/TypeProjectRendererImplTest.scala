package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class TypeProjectRendererImplTest extends UnitTestSuite {

  private val typeRenderer = mock[TypeRenderer]
  private val typeNameRenderer = mock[TypeNameRenderer]

  private val typeProjectRenderer = new TypeProjectRendererImpl(
    typeRenderer,
    typeNameRenderer
  )

  test("traverse") {
    val `type` = t"MyClass"
    val innerType = t"MyInnerClass"
    val typeProject = t"MyClass#MyInnerClass"

    doWrite("MyClass").when(typeRenderer).render(eqTree(`type`))
    doWrite("MyInnerClass").when(typeNameRenderer).render(eqTree(innerType))

    typeProjectRenderer.render(typeProject)

    outputWriter.toString shouldBe "MyClass.MyInnerClass"
  }

}
