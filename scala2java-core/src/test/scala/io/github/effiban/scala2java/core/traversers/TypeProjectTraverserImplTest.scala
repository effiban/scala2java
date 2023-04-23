package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeNameRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Type

class TypeProjectTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val typeNameTraverser = mock[TypeNameTraverser]
  private val typeNameRenderer = mock[TypeNameRenderer]

  private val typeProjectTraverser = new TypeProjectTraverserImpl(
    typeTraverser,
    typeNameTraverser,
    typeNameRenderer
  )

  test("traverse") {
    val tpe = Type.Name("MyClass")
    val innerType = Type.Name("MyInnerClass")
    val traversedInnerType = Type.Name("MyTraversedInnerClass")
    val typeProject = Type.Project(qual = tpe, name = innerType)

    doWrite("MyClass").when(typeTraverser).traverse(eqTree(tpe))
    doReturn(traversedInnerType).when(typeNameTraverser).traverse(eqTree(innerType))
    doWrite("MyTraversedInnerClass").when(typeNameRenderer).render(eqTree(traversedInnerType))

    typeProjectTraverser.traverse(typeProject)

    outputWriter.toString shouldBe "MyClass.MyTraversedInnerClass"
  }

}
