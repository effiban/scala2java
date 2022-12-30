package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.typeinferrers.ApplyTypeTypeInferrer

class ExtendedTypeInferrersTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]
  private val extensions = List(extension1, extension2)


  test("applyTypeTypeInferrers") {
    val applyTypeTypeInferrer1 = mock[ApplyTypeTypeInferrer]
    val applyTypeTypeInferrer2 = mock[ApplyTypeTypeInferrer]
    val applyTypeTypeInferrers = List(applyTypeTypeInferrer1, applyTypeTypeInferrer2)

    when(extension1.applyTypeTypeInferrer()).thenReturn(applyTypeTypeInferrer1)
    when(extension2.applyTypeTypeInferrer()).thenReturn(applyTypeTypeInferrer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.applyTypeTypeInferrers shouldBe applyTypeTypeInferrers
  }
}
