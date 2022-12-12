package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.Term

class CompositeTermSelectTransformerTest extends UnitTestSuite {

  private val InitialTermSelect = termSelectWithName("initial")

  private val coreTransformer = mock[TermSelectTransformer]
  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  test("transform when there are two extension transformers") {
    val transformedTermSelect1 = termSelectWithName("transformed1")
    val transformedTermSelect2 = termSelectWithName("transformed2")
    val transformedTermSelect3 = termSelectWithName("transformed3")

    val extensionTransformer1 = mock[TermSelectTransformer]
    val extensionTransformer2 = mock[TermSelectTransformer]
    val extensionTransformers = List(extensionTransformer1, extensionTransformer2)

    when(extensionRegistry.termSelectTransformers).thenReturn(extensionTransformers)
    when(extensionTransformer1.transform(eqTree(InitialTermSelect))).thenReturn(transformedTermSelect1)
    when(extensionTransformer2.transform(eqTree(transformedTermSelect1))).thenReturn(transformedTermSelect2)
    when(coreTransformer.transform(eqTree(transformedTermSelect2))).thenReturn(transformedTermSelect3)

    compositeTransformer().transform(InitialTermSelect).structure shouldBe transformedTermSelect3.structure
  }

  test("transform when there is one extension transformer") {
    val transformedTermSelect1 = termSelectWithName("transformed1")
    val transformedTermSelect2 = termSelectWithName("transformed2")

    val extensionTransformer = mock[TermSelectTransformer]
    val extensionTransformers = List(extensionTransformer)

    when(extensionRegistry.termSelectTransformers).thenReturn(extensionTransformers)
    when(extensionTransformer.transform(eqTree(InitialTermSelect))).thenReturn(transformedTermSelect1)
    when(coreTransformer.transform(eqTree(transformedTermSelect1))).thenReturn(transformedTermSelect2)

    compositeTransformer().transform(InitialTermSelect).structure shouldBe transformedTermSelect2.structure
  }

  test("transform when there are no extension transformers") {
    val transformedTermSelect = termSelectWithName("transformed")

    when(extensionRegistry.termSelectTransformers).thenReturn(Nil)
    when(coreTransformer.transform(eqTree(InitialTermSelect))).thenReturn(transformedTermSelect)

    compositeTransformer().transform(InitialTermSelect).structure shouldBe transformedTermSelect.structure
  }

  private def termSelectWithName(name: String) = Term.Select(Term.Name("qualifier"), Term.Name(name))

  private def compositeTransformer() = new CompositeTermSelectTransformer(coreTransformer)
}
