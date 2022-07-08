package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import com.effiban.scala2java.testtrees.TypeNames

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
