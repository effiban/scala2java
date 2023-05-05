package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class ClassOfTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]

  private val classOfTraverser = new ClassOfTraverserImpl(typeTraverser, typeRenderer)

  test("traverse() when there is one type should output the Java equivalent") {
    val typeName = t"T"
    val traversedTypeName = t"U"

    doReturn(traversedTypeName).when(typeTraverser).traverse(eqTree(typeName))
    doWrite("U").when(typeRenderer).render(eqTree(traversedTypeName))

    classOfTraverser.traverse(List(typeName))

    outputWriter.toString shouldBe "U.class"
  }

  test("traverse() when there are no types should output an error comment") {
    classOfTraverser.traverse(Nil)

    outputWriter.toString shouldBe "UNPARSEABLE 'classOf' with types: (none)"
  }

  test("traverse() when there are two types should output an error comment") {
    classOfTraverser.traverse(List(t"T", t"U"))

    outputWriter.toString shouldBe "UNPARSEABLE 'classOf' with types: T, U"
  }
}
