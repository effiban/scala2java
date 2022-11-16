package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.ClassNameTransformer

import scala.meta.Type

class CompositeClassNameTransformerTest extends UnitTestSuite {

  private val InitialClassName = Type.Name("Initial")

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  test("transform when there are two transformers") {
    val transformedClassName1 = Type.Name("Transformed1")
    val transformedClassName2 = Type.Name("Transformed2")

    val transformer1 = mock[ClassNameTransformer]
    val transformer2 = mock[ClassNameTransformer]
    val transformers = List(transformer1, transformer2)

    when(transformer1.transform(eqTree(InitialClassName))).thenReturn(transformedClassName1)
    when(transformer2.transform(eqTree(transformedClassName1))).thenReturn(transformedClassName2)

    when(extensionRegistry.classNameTransformers).thenReturn(transformers)

    new CompositeClassNameTransformer().transform(InitialClassName).structure shouldBe transformedClassName2.structure
  }

  test("transform when there is one transformer") {
    val transformedClassName = Type.Name("Transformed")

    val transformer = mock[ClassNameTransformer]
    val transformers = List(transformer)

    when(transformer.transform(eqTree(InitialClassName))).thenReturn(transformedClassName)

    when(extensionRegistry.classNameTransformers).thenReturn(transformers)

    new CompositeClassNameTransformer().transform(InitialClassName).structure shouldBe transformedClassName.structure
  }

  test("transform when there are no transformers") {
    when(extensionRegistry.classNameTransformers).thenReturn(Nil)

    new CompositeClassNameTransformer().transform(InitialClassName).structure shouldBe InitialClassName.structure
  }
}
