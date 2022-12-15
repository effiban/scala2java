package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TermApplyTypeToTermApplyTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type}

class CompositeTermApplyTypeToTermApplyTransformerTest extends UnitTestSuite {

  private val TheTermApplyType = termApplyTypeWithFun("fun")
  private val TheTermApply = termApplyWithFun("fun")

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  private val transformer1 = mock[TermApplyTypeToTermApplyTransformer]
  private val transformer2 = mock[TermApplyTypeToTermApplyTransformer]

  private val compositeTransformer = new CompositeTermApplyTypeToTermApplyTransformer()

  test("transform when there are no transformers - should return empty") {
    when(extensionRegistry.termApplyTypeToTermApplyTransformers).thenReturn(Nil)

    compositeTransformer.transform(TheTermApplyType) shouldBe None
  }

  test("transform when there is one transformer returning non-empty should return its result") {
    when(extensionRegistry.termApplyTypeToTermApplyTransformers).thenReturn(List(transformer1))
    when(transformer1.transform(eqTree(TheTermApplyType))).thenReturn(Some(TheTermApply))

    compositeTransformer.transform(TheTermApplyType).value.structure shouldBe TheTermApply.structure
  }

  test("transform when there are two transformers and first returns non-empty should return result of first") {
    when(extensionRegistry.termApplyTypeToTermApplyTransformers).thenReturn(List(transformer1, transformer2))
    when(transformer1.transform(eqTree(TheTermApplyType))).thenReturn(Some(TheTermApply))

    compositeTransformer.transform(TheTermApplyType).value.structure shouldBe TheTermApply.structure
  }

  test("transform when there are two transformers, first returns empty and second returns non-empty - should return result of second") {
    when(extensionRegistry.termApplyTypeToTermApplyTransformers).thenReturn(List(transformer1, transformer2))
    when(transformer1.transform(eqTree(TheTermApplyType))).thenReturn(None)
    when(transformer2.transform(eqTree(TheTermApplyType))).thenReturn(Some(TheTermApply))

    compositeTransformer.transform(TheTermApplyType).value.structure shouldBe TheTermApply.structure
  }

  test("transform when there are two transformers, both returning empty - should return empty") {
    when(extensionRegistry.termApplyTypeToTermApplyTransformers).thenReturn(List(transformer1, transformer2))
    when(transformer1.transform(eqTree(TheTermApplyType))).thenReturn(None)
    when(transformer2.transform(eqTree(TheTermApplyType))).thenReturn(None)

    compositeTransformer.transform(TheTermApplyType) shouldBe None
  }

  private def termApplyTypeWithFun(name: String) = Term.ApplyType(Term.Name(name), List(Type.Name("T")))

  private def termApplyWithFun(name: String) = Term.Apply(Term.Name(name), Nil)

}
