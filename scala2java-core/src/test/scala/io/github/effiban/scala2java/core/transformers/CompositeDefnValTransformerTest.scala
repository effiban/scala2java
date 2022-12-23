package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnValTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.XtensionQuasiquoteTerm

class CompositeDefnValTransformerTest extends UnitTestSuite {

  private val TheJavaScope = JavaScope.Class

  private val defnVal1 = q"val v1: Int = 3"
  private val defnVal2 = q"val v2: Int = 3"
  private val defnVal3 = q"val v3: Int = 3"

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  private val transformer1 = mock[DefnValTransformer]
  private val transformer2 = mock[DefnValTransformer]

  private val compositeTransformer = new CompositeDefnValTransformer()

  test("transform when there are no transformers - should return same object") {
    when(extensionRegistry.defnValTransformers).thenReturn(Nil)

    compositeTransformer.transform(defnVal1, TheJavaScope).structure shouldBe defnVal1.structure
  }

  test("transform when there is one transformer should return its result") {
    when(extensionRegistry.defnValTransformers).thenReturn(List(transformer1))
    when(transformer1.transform(eqTree(defnVal1), ArgumentMatchers.eq(TheJavaScope))).thenReturn(defnVal2)

    compositeTransformer.transform(defnVal1, TheJavaScope).structure shouldBe defnVal2.structure
  }

  test("transform when there are two transformers should return result of second") {
    when(extensionRegistry.defnValTransformers).thenReturn(List(transformer1, transformer2))
    when(transformer1.transform(eqTree(defnVal1), ArgumentMatchers.eq(TheJavaScope))).thenAnswer(defnVal2)
    when(transformer2.transform(eqTree(defnVal2), ArgumentMatchers.eq(TheJavaScope))).thenReturn(defnVal3)

    compositeTransformer.transform(defnVal1, TheJavaScope).structure shouldBe defnVal3.structure
  }
}
