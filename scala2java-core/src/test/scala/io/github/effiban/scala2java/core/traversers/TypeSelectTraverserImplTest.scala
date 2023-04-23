package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeNameRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type}

class TypeSelectTraverserImplTest extends UnitTestSuite {

  private val defaultTermRefTraverser = mock[TermRefTraverser]
  private val typeNameTraverser = mock[TypeNameTraverser]
  private val typeNameRenderer = mock[TypeNameRenderer]

  private val typeSelectTraverser = new TypeSelectTraverserImpl(
    defaultTermRefTraverser,
    typeNameTraverser,
    typeNameRenderer
  )

  test("traverse()") {
    val qual = Term.Name("myObj")
    val tpe = Type.Name("MyType")
    val traversedType = Type.Name("MyTraversedType")

    val typeSelect = Type.Select(qual, tpe)

    doWrite("myObj").when(defaultTermRefTraverser).traverse(eqTree(qual))
    doReturn(traversedType).when(typeNameTraverser).traverse(eqTree(tpe))
    doWrite("MyTraversedType").when(typeNameRenderer).render(eqTree(traversedType))

    typeSelectTraverser.traverse(typeSelect)

    outputWriter.toString shouldBe "myObj.MyTraversedType"
  }
}
