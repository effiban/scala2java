package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.typeinferrers.{ApplyDeclDefInferrer, NameTypeInferrer, SelectTypeInferrer}

class ExtendedTypeInferrersTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]
  private val extensions = List(extension1, extension2)


  test("nameTypeInferrers") {
    val nameTypeInferrer1 = mock[NameTypeInferrer]
    val nameTypeInferrer2 = mock[NameTypeInferrer]
    val nameTypeInferrers = List(nameTypeInferrer1, nameTypeInferrer2)

    when(extension1.nameTypeInferrer()).thenReturn(nameTypeInferrer1)
    when(extension2.nameTypeInferrer()).thenReturn(nameTypeInferrer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.nameTypeInferrers shouldBe nameTypeInferrers
  }

  test("applyDeclDefInferrers") {
    val applyDeclDefInferrer1 = mock[ApplyDeclDefInferrer]
    val applyDeclDefInferrer2 = mock[ApplyDeclDefInferrer]
    val applyDeclDefInferrers = List(applyDeclDefInferrer1, applyDeclDefInferrer2)

    when(extension1.applyDeclDefInferrer()).thenReturn(applyDeclDefInferrer1)
    when(extension2.applyDeclDefInferrer()).thenReturn(applyDeclDefInferrer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.applyDeclDefInferrers shouldBe applyDeclDefInferrers
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
