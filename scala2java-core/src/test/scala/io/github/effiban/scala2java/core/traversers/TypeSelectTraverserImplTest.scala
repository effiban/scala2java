package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type}

class TypeSelectTraverserImplTest extends UnitTestSuite {

  private val termRefTraverser = mock[TermRefTraverser]
  private val typeNameTraverser = mock[TypeNameTraverser]

  private val typeSelectTraverser = new TypeSelectTraverserImpl(termRefTraverser, typeNameTraverser)

  test("traverse()") {
    val qual = Term.Name("myObj")
    val tpe = Type.Name("MyType")

    val typeSelect = Type.Select(qual, tpe)

    doWrite("myObj").when(termRefTraverser).traverse(eqTree(qual))
    doWrite("MyType").when(typeNameTraverser).traverse(eqTree(tpe))

    typeSelectTraverser.traverse(typeSelect)

    outputWriter.toString shouldBe "myObj.MyType"
  }
}
