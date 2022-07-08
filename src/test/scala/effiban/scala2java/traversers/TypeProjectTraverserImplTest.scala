package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

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