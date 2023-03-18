package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.typeinferrers.{ApplyTypeInferrer, ApplyTypeTypeInferrer, NameTypeInferrer, SelectTypeInferrer}

class ExtendedTypeInferrersTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]
  private val extensions = List(extension1, extension2)


  test("applyTypeInferrers") {
    val applyTypeInferrer1 = mock[ApplyTypeInferrer]
    val applyTypeInferrer2 = mock[ApplyTypeInferrer]
    val applyTypeInferrers = List(applyTypeInferrer1, applyTypeInferrer2)

    when(extension1.applyTypeInferrer()).thenReturn(applyTypeInferrer1)
    when(extension2.applyTypeInferrer()).thenReturn(applyTypeInferrer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.applyTypeInferrers shouldBe applyTypeInferrers
  }

  test("applyTypeTypeInferrers") {
    val applyTypeTypeInferrer1 = mock[ApplyTypeTypeInferrer]
    val applyTypeTypeInferrer2 = mock[ApplyTypeTypeInferrer]
    val applyTypeTypeInferrers = List(applyTypeTypeInferrer1, applyTypeTypeInferrer2)

    when(extension1.applyTypeTypeInferrer()).thenReturn(applyTypeTypeInferrer1)
    when(extension2.applyTypeTypeInferrer()).thenReturn(applyTypeTypeInferrer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.applyTypeTypeInferrers shouldBe applyTypeTypeInferrers
  }

  test("nameTypeInferrers") {
    val nameTypeInferrer1 = mock[NameTypeInferrer]
    val nameTypeInferrer2 = mock[NameTypeInferrer]
    val nameTypeInferrers = List(nameTypeInferrer1, nameTypeInferrer2)

    when(extension1.nameTypeInferrer()).thenReturn(nameTypeInferrer1)
    when(extension2.nameTypeInferrer()).thenReturn(nameTypeInferrer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.nameTypeInferrers shouldBe nameTypeInferrers
  }

  test("selectTypeInferrers") {
    val selectTypeInferrer1 = mock[SelectTypeInferrer]
    val selectTypeInferrer2 = mock[SelectTypeInferrer]
    val selectTypeInferrers = List(selectTypeInferrer1, selectTypeInferrer2)

    when(extension1.selectTypeInferrer()).thenReturn(selectTypeInferrer1)
    when(extension2.selectTypeInferrer()).thenReturn(selectTypeInferrer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.selectTypeInferrers shouldBe selectTypeInferrers
  }
}
