package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.meta.Term

class CompositeTermApplyTransformerTest extends UnitTestSuite {

  private val InitialTermApply = termApplyWithFun("initial")

  private val coreTransformer = mock[TermApplyTransformer]
  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  test("transform when there are two extension transformers") {
    val transformedTermApply1 = termApplyWithFun("transformed1")
    val transformedTermApply2 = termApplyWithFun("transformed2")
    val transformedTermApply3 = termApplyWithFun("transformed3")

    val extensionTransformer1 = mock[TermApplyTransformer]
    val extensionTransformer2 = mock[TermApplyTransformer]
    val extensionTransformers = List(extensionTransformer1, extensionTransformer2)

    when(extensionRegistry.termApplyTransformers).thenReturn(extensionTransformers)
    when(extensionTransformer1.transform(eqTree(InitialTermApply))).thenReturn(transformedTermApply1)
    when(extensionTransformer2.transform(eqTree(transformedTermApply1))).thenReturn(transformedTermApply2)
    when(coreTransformer.transform(eqTree(transformedTermApply2))).thenReturn(transformedTermApply3)

    compositeTransformer().transform(InitialTermApply).structure shouldBe transformedTermApply3.structure
  }

  test("transform when there is one extension transformer") {
    val transformedTermApply1 = termApplyWithFun("transformed1")
    val transformedTermApply2 = termApplyWithFun("transformed2")

    val extensionTransformer = mock[TermApplyTransformer]
    val extensionTransformers = List(extensionTransformer)

    when(extensionRegistry.termApplyTransformers).thenReturn(extensionTransformers)
    when(extensionTransformer.transform(eqTree(InitialTermApply))).thenReturn(transformedTermApply1)
    when(coreTransformer.transform(eqTree(transformedTermApply1))).thenReturn(transformedTermApply2)

    compositeTransformer().transform(InitialTermApply).structure shouldBe transformedTermApply2.structure
  }

  test("transform when there are no extension transformers") {
    val transformedTermApply = termApplyWithFun("transformed")

    when(extensionRegistry.termApplyTransformers).thenReturn(Nil)
    when(coreTransformer.transform(eqTree(InitialTermApply))).thenReturn(transformedTermApply)

    compositeTransformer().transform(InitialTermApply).structure shouldBe transformedTermApply.structure
  }

  private def termApplyWithFun(fun: String) = {
    Term.Apply(Term.Name(fun), List(Term.Name("x"), Term.Name("y")))
  }

  private def compositeTransformer() = new CompositeTermApplyTransformer(coreTransformer)
}
