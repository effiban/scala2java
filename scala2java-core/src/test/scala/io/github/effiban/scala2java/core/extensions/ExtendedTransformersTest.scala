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

  test("defnValTransformers") {
    val defnValTransformer1 = mock[DefnValTransformer]
    val defnValTransformer2 = mock[DefnValTransformer]
    val defnValTransformers = List(defnValTransformer1, defnValTransformer2)

    when(extension1.defnValTransformer()).thenReturn(defnValTransformer1)
    when(extension2.defnValTransformer()).thenReturn(defnValTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.defnValTransformers shouldBe defnValTransformers
  }

  test("defnValToDeclVarTransformers") {
    val defnValToDeclVarTransformer1 = mock[DefnValToDeclVarTransformer]
    val defnValToDeclVarTransformer2 = mock[DefnValToDeclVarTransformer]
    val defnValToDeclVarTransformers = List(defnValToDeclVarTransformer1, defnValToDeclVarTransformer2)

    when(extension1.defnValToDeclVarTransformer()).thenReturn(defnValToDeclVarTransformer1)
    when(extension2.defnValToDeclVarTransformer()).thenReturn(defnValToDeclVarTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.defnValToDeclVarTransformers shouldBe defnValToDeclVarTransformers
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

  test("termApplyTypeToTermApplyTransformers") {
    val termApplyTypeToTermApplyTransformer1 = mock[TermApplyTypeToTermApplyTransformer]
    val termApplyTypeToTermApplyTransformer2 = mock[TermApplyTypeToTermApplyTransformer]
    val termApplyTypeToTermApplyTransformers = List(termApplyTypeToTermApplyTransformer1, termApplyTypeToTermApplyTransformer2)

    when(extension1.termApplyTypeToTermApplyTransformer()).thenReturn(termApplyTypeToTermApplyTransformer1)
    when(extension2.termApplyTypeToTermApplyTransformer()).thenReturn(termApplyTypeToTermApplyTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termApplyTypeToTermApplyTransformers shouldBe termApplyTypeToTermApplyTransformers
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

  test("typeNameTransformers") {
    val typeNameTransformer1 = mock[TypeNameTransformer]
    val typeNameTransformer2 = mock[TypeNameTransformer]
    val typeNameTransformers = List(typeNameTransformer1, typeNameTransformer2)

    when(extension1.typeNameTransformer()).thenReturn(typeNameTransformer1)
    when(extension2.typeNameTransformer()).thenReturn(typeNameTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.typeNameTransformers shouldBe typeNameTransformers
  }
}
