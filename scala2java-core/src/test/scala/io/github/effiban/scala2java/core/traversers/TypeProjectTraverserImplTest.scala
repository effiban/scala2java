package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Type

class TypeProjectTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val typeNameTraverser = mock[TypeNameTraverser]

  private val typeProjectTraverser = new TypeProjectTraverserImpl(typeTraverser, typeNameTraverser)

  test("traverse") {
    val tpe = Type.Name("MyClass")
    val innerType = Type.Name("MyInnerClass")
    val typeProject = Type.Project(qual = tpe, name = innerType)

    doWrite("MyClass").when(typeTraverser).traverse(eqTree(tpe))
    doWrite("MyInnerClass").when(typeNameTraverser).traverse(eqTree(innerType))

    typeProjectTraverser.traverse(typeProject)

    outputWriter.toString shouldBe "MyClass.MyInnerClass"
  }

}
