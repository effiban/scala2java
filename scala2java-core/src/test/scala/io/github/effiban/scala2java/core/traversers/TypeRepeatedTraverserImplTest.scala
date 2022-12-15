package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

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
