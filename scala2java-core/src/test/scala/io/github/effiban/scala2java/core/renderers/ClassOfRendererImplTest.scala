package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ClassOfRendererImplTest extends UnitTestSuite {

  private val typeRenderer = mock[TypeRenderer]

  private val classOfRenderer = new ClassOfRendererImpl(typeRenderer)

  test("traverse() when there is one type should output the Java equivalent") {
    val typeName = t"T"
    val classOf = q"classOf[T]"

    doWrite("T").when(typeRenderer).render(eqTree(typeName))

    classOfRenderer.render(classOf)

    outputWriter.toString shouldBe "T.class"
  }

  test("traverse() when there are two types should output an error comment") {
    classOfRenderer.render(q"classOf[T, U]")

    outputWriter.toString shouldBe "UNPARSEABLE 'classOf' with types: T, U"
  }
}
