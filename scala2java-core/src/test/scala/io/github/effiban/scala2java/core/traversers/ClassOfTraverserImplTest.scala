package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ClassOfRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ClassOfTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val classOfRenderer = mock[ClassOfRenderer]

  private val classOfTraverser = new ClassOfTraverserImpl(typeTraverser, classOfRenderer)

  test("traverse() when there is one type") {
    val typeName = t"T"
    val classOf = q"classOf[T]"
    val traversedTypeName = t"U"
    val traversedClassOf = q"classOf[U]"

    doReturn(traversedTypeName).when(typeTraverser).traverse(eqTree(typeName))
    doWrite("U.class").when(classOfRenderer).render(eqTree(traversedClassOf))

    classOfTraverser.traverse(classOf)

    outputWriter.toString shouldBe "U.class"
  }
}
