package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.FileNameTransformer

class CompositeFileNameTransformerTest extends UnitTestSuite {

  private val InitialFileName = "Initial"

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  test("transform when there are two transformers") {
    val transformedFileName1 = "Transformed1"
    val transformedFileName2 = "Transformed2"

    val transformer1 = mock[FileNameTransformer]
    val transformer2 = mock[FileNameTransformer]
    val transformers = List(transformer1, transformer2)

    when(transformer1.transform(InitialFileName)).thenReturn(transformedFileName1)
    when(transformer2.transform(transformedFileName1)).thenReturn(transformedFileName2)

    when(extensionRegistry.fileNameTransformers).thenReturn(transformers)

    new CompositeFileNameTransformer().transform(InitialFileName) shouldBe transformedFileName2
  }

  test("transform when there is one transformer") {
    val transformedFileName = "Transformed"

    val transformer = mock[FileNameTransformer]
    val transformers = List(transformer)

    when(transformer.transform(InitialFileName)).thenReturn(transformedFileName)

    when(extensionRegistry.fileNameTransformers).thenReturn(transformers)

    new CompositeFileNameTransformer().transform(InitialFileName) shouldBe transformedFileName
  }

  test("transform when there are no transformers") {
    when(extensionRegistry.fileNameTransformers).thenReturn(Nil)

    new CompositeFileNameTransformer().transform(InitialFileName) shouldBe InitialFileName
  }
}
