package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TypeNames

import scala.meta.Type

class TypeRepeatedTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  val typeRepeatedTraverser = new TypeRepeatedTraverserImpl(typeTraverser)

  test("traverse()") {
    val repeatedType = Type.Repeated(TypeNames.String)

    doWrite("String").when(typeTraverser).traverse(eqTree(TypeNames.String))

    typeRepeatedTraverser.traverse(repeatedType)

    outputWriter.toString shouldBe "String..."
  }
}
