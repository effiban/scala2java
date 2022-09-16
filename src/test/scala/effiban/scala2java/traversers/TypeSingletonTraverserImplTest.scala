package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.transformers.TypeSingletonToTermTransformer

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
