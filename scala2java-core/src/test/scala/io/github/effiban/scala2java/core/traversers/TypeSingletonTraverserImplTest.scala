package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.TypeSingletonToTermTransformer

import scala.meta.{Term, Type}

class TypeSingletonTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val typeSingletonTransformer = mock[TypeSingletonToTermTransformer]

  private val typeSingletonTraverser = new TypeSingletonTraverserImpl(termTraverser, typeSingletonTransformer)

  test("traverse") {
    val initialTermRef = Term.Name("initial")
    val transformedTermRef = Term.Name("transformed")
    val singletonType = Type.Singleton(initialTermRef)

    when(typeSingletonTransformer.transform(eqTree(singletonType))).thenReturn(transformedTermRef)
    doWrite("transformed").when(termTraverser).traverse(eqTree(transformedTermRef))

    typeSingletonTraverser.traverse(singletonType)

    outputWriter.toString shouldBe "transformed"
  }

}
