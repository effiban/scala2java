package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.transformers._

class ExtendedTransformersTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]
  private val extensions = List(extension1, extension2)

  test("fileNameTransformers") {
    val fileNameTransformer1 = mock[FileNameTransformer]
    val fileNameTransformer2 = mock[FileNameTransformer]
    val fileNameTransformers = List(fileNameTransformer1, fileNameTransformer2)

    when(extension1.fileNameTransformer()).thenReturn(fileNameTransformer1)
    when(extension2.fileNameTransformer()).thenReturn(fileNameTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.fileNameTransformers shouldBe fileNameTransformers
  }

  test("importerTransformers") {
    val importerTransformer1 = mock[ImporterTransformer]
    val importerTransformer2 = mock[ImporterTransformer]
    val importerTransformers = List(importerTransformer1, importerTransformer2)

    when(extension1.importerTransformer()).thenReturn(importerTransformer1)
    when(extension2.importerTransformer()).thenReturn(importerTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.importerTransformers shouldBe importerTransformers
  }

  test("classTransformers") {
    val classTransformer1 = mock[ClassTransformer]
    val classTransformer2 = mock[ClassTransformer]
    val classTransformers = List(classTransformer1, classTransformer2)

    when(extension1.classTransformer()).thenReturn(classTransformer1)
    when(extension2.classTransformer()).thenReturn(classTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.classTransformers shouldBe classTransformers
  }

  test("templateTermApplyInfixToDefnTransformers") {
    val templateTermApplyInfixToDefnTransformer1 = mock[TemplateTermApplyInfixToDefnTransformer]
    val templateTermApplyInfixToDefnTransformer2 = mock[TemplateTermApplyInfixToDefnTransformer]
    val templateTermApplyInfixToDefnTransformers = List(templateTermApplyInfixToDefnTransformer1, templateTermApplyInfixToDefnTransformer2)

    when(extension1.templateTermApplyInfixToDefnTransformer()).thenReturn(templateTermApplyInfixToDefnTransformer1)
    when(extension2.templateTermApplyInfixToDefnTransformer()).thenReturn(templateTermApplyInfixToDefnTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.templateTermApplyInfixToDefnTransformers shouldBe templateTermApplyInfixToDefnTransformers
  }

  test("templateTermApplyToDefnTransformers") {
    val templateTermApplyToDefnTransformer1 = mock[TemplateTermApplyToDefnTransformer]
    val templateTermApplyToDefnTransformer2 = mock[TemplateTermApplyToDefnTransformer]
    val templateTermApplyToDefnTransformers = List(templateTermApplyToDefnTransformer1, templateTermApplyToDefnTransformer2)

    when(extension1.templateTermApplyToDefnTransformer()).thenReturn(templateTermApplyToDefnTransformer1)
    when(extension2.templateTermApplyToDefnTransformer()).thenReturn(templateTermApplyToDefnTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.templateTermApplyToDefnTransformers shouldBe templateTermApplyToDefnTransformers
  }

  test("defnVarTransformers") {
    val defnVarTransformer1 = mock[DefnVarTransformer]
    val defnVarTransformer2 = mock[DefnVarTransformer]
    val defnVarTransformers = List(defnVarTransformer1, defnVarTransformer2)

    when(extension1.defnVarTransformer()).thenReturn(defnVarTransformer1)
    when(extension2.defnVarTransformer()).thenReturn(defnVarTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.defnVarTransformers shouldBe defnVarTransformers
  }

  test("defnVarToDeclVarTransformers") {
    val defnVarToDeclVarTransformer1 = mock[DefnVarToDeclVarTransformer]
    val defnVarToDeclVarTransformer2 = mock[DefnVarToDeclVarTransformer]
    val defnVarToDeclVarTransformers = List(defnVarToDeclVarTransformer1, defnVarToDeclVarTransformer2)

    when(extension1.defnVarToDeclVarTransformer()).thenReturn(defnVarToDeclVarTransformer1)
    when(extension2.defnVarToDeclVarTransformer()).thenReturn(defnVarToDeclVarTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.defnVarToDeclVarTransformers shouldBe defnVarToDeclVarTransformers
  }

  test("defnDefTransformers") {
    val defnDefTransformer1 = mock[DefnDefTransformer]
    val defnDefTransformer2 = mock[DefnDefTransformer]
    val defnDefTransformers = List(defnDefTransformer1, defnDefTransformer2)

    when(extension1.defnDefTransformer()).thenReturn(defnDefTransformer1)
    when(extension2.defnDefTransformer()).thenReturn(defnDefTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.defnDefTransformers shouldBe defnDefTransformers
  }

  test("termApplyInfixToTermApplyTransformers") {
    val termApplyInfixToTermApplyTransformer1 = mock[TermApplyInfixToTermApplyTransformer]
    val termApplyInfixToTermApplyTransformer2 = mock[TermApplyInfixToTermApplyTransformer]
    val termApplyInfixToTermApplyTransformers = List(termApplyInfixToTermApplyTransformer1, termApplyInfixToTermApplyTransformer2)

    when(extension1.termApplyInfixToTermApplyTransformer()).thenReturn(termApplyInfixToTermApplyTransformer1)
    when(extension2.termApplyInfixToTermApplyTransformer()).thenReturn(termApplyInfixToTermApplyTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termApplyInfixToTermApplyTransformers shouldBe termApplyInfixToTermApplyTransformers
  }

  test("termApplyTransformers") {
    val termApplyTransformer1 = mock[TermApplyTransformer]
    val termApplyTransformer2 = mock[TermApplyTransformer]
    val termApplyTransformers = List(termApplyTransformer1, termApplyTransformer2)

    when(extension1.termApplyTransformer()).thenReturn(termApplyTransformer1)
    when(extension2.termApplyTransformer()).thenReturn(termApplyTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termApplyTransformers shouldBe termApplyTransformers
  }

  test("termSelectTransformers") {
    val termSelectTransformer1 = mock[TermSelectTransformer]
    val termSelectTransformer2 = mock[TermSelectTransformer]
    val termSelectTransformers = List(termSelectTransformer1, termSelectTransformer2)

    when(extension1.termSelectTransformer()).thenReturn(termSelectTransformer1)
    when(extension2.termSelectTransformer()).thenReturn(termSelectTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termSelectTransformers shouldBe termSelectTransformers
  }

  test("termNameTransformers") {
    val termNameTransformer1 = mock[TermNameTransformer]
    val termNameTransformer2 = mock[TermNameTransformer]
    val termNameTransformers = List(termNameTransformer1, termNameTransformer2)

    when(extension1.termNameTransformer()).thenReturn(termNameTransformer1)
    when(extension2.termNameTransformer()).thenReturn(termNameTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termNameTransformers shouldBe termNameTransformers
  }

  test("typeSelectTransformers") {
    val typeSelectTransformer1 = mock[TypeSelectTransformer]
    val typeSelectTransformer2 = mock[TypeSelectTransformer]
    val typeSelectTransformers = List(typeSelectTransformer1, typeSelectTransformer2)

    when(extension1.typeSelectTransformer()).thenReturn(typeSelectTransformer1)
    when(extension2.typeSelectTransformer()).thenReturn(typeSelectTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.typeSelectTransformers shouldBe typeSelectTransformers
  }
}
