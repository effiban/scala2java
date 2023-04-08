package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.TypeSingletonToTermTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type}

class TypeSingletonTraverserImplTest extends UnitTestSuite {

  private val defaultTermTraverser = mock[DefaultTermTraverser]
  private val typeSingletonTransformer = mock[TypeSingletonToTermTransformer]

  private val typeSingletonTraverser = new TypeSingletonTraverserImpl(defaultTermTraverser, typeSingletonTransformer)

  test("traverse") {
    val initialTermRef = Term.Name("initial")
    val transformedTermRef = Term.Name("transformed")
    val singletonType = Type.Singleton(initialTermRef)

    when(typeSingletonTransformer.transform(eqTree(singletonType))).thenReturn(transformedTermRef)
    doWrite("transformed").when(defaultTermTraverser).traverse(eqTree(transformedTermRef))

    typeSingletonTraverser.traverse(singletonType)

    outputWriter.toString shouldBe "transformed"
  }

}
