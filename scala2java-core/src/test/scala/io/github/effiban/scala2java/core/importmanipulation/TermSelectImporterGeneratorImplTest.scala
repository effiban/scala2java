package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.entities.TermSelects.ScalaArray
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Term, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class TermSelectImporterGeneratorImplTest extends UnitTestSuite {

  private val qualifiedNameImporterGenerator = mock[QualifiedNameImporterGenerator]

  private val termSelectImporterGenerator = new TermSelectImporterGeneratorImpl(qualifiedNameImporterGenerator)

  test("generate() for 'A.b' when identified by the inner generator as a Java static field, should return the result of the inner generator") {
    val termSelect = q"A.b"
    val expectedImporter = importer"A.b"

    when(qualifiedNameImporterGenerator.generateForStaticField(eqTree(q"A"), eqTo("b"))).thenReturn(Some(expectedImporter))

    termSelectImporterGenerator.generate(termSelect).value.structure shouldBe expectedImporter.structure
  }

  test("generate() for 'A.B.c' when identified by the inner generator as a Java static field, should return the result of the inner generator") {
    val termSelect = q"A.B.c"
    val expectedImporter = importer"A.B.c"

    when(qualifiedNameImporterGenerator.generateForStaticField(eqTree(q"A.B"), eqTo("c"))).thenReturn(Some(expectedImporter))

    termSelectImporterGenerator.generate(termSelect).value.structure shouldBe expectedImporter.structure
  }

  test("generate() for 'A.B' when identified by the inner generator as a Java type, should return the result of the inner generator") {
    val termSelect = q"A.B"
    val expectedImporter = importer"A.B"

    when(qualifiedNameImporterGenerator.generateForStaticField(eqTree(q"A"), eqTo("B"))).thenReturn(None)
    when(qualifiedNameImporterGenerator.generateForType(eqTree(q"A"), eqTo("B"))).thenReturn(Some(expectedImporter))

    termSelectImporterGenerator.generate(termSelect).value.structure shouldBe expectedImporter.structure
  }

  test("generate() for 'A.B.C' when identified by the inner generator as a Java type, should return the result of the inner generator") {
    val termSelect = q"A.B.C"
    val expectedImporter = importer"A.B.C"

    when(qualifiedNameImporterGenerator.generateForStaticField(eqTree(q"A.B"), eqTo("C"))).thenReturn(None)
    when(qualifiedNameImporterGenerator.generateForType(eqTree(q"A.B"), eqTo("C"))).thenReturn(Some(expectedImporter))

    termSelectImporterGenerator.generate(termSelect).value.structure shouldBe expectedImporter.structure
  }

  test("generate() for 'A.b' when not identified by the inner generator as static field or a type, should return None") {
    val termSelect = q"A.b"

    when(qualifiedNameImporterGenerator.generateForStaticField(eqTree(q"A"), eqTo("b"))).thenReturn(None)
    when(qualifiedNameImporterGenerator.generateForType(eqTree(q"A"), eqTo("b"))).thenReturn(None)

    termSelectImporterGenerator.generate(termSelect) shouldBe None
  }

  test("generate() for 'a.b(3)' should not call the inner generator and should return None") {
    val termApply = q"a.b(3)"
    val termSelect = termApply.fun.asInstanceOf[Term.Select]

    termSelectImporterGenerator.generate(termSelect) shouldBe None
    
    verifyNoMoreInteractions(qualifiedNameImporterGenerator)
  }

  test("generate() for a qualified name in a Term.ApplyType should not call the inner generator and should return None") {
    val termApplyType = q"a.b[C]"
    val termSelect = termApplyType.fun.asInstanceOf[Term.Select]

    termSelectImporterGenerator.generate(termSelect) shouldBe None

    verifyNoMoreInteractions(qualifiedNameImporterGenerator)
  }

  test("generate() for a qualified name that is not a path should not call the inner generator and should return None") {
    val termSelect = q"(a.calculate(b)).c.d"

    termSelectImporterGenerator.generate(termSelect) shouldBe None

    verifyNoMoreInteractions(qualifiedNameImporterGenerator)
  }

  test("generate() for a Scala Array should not call the inner generator and should return None") {
    termSelectImporterGenerator.generate(ScalaArray) shouldBe None
    
    verifyNoMoreInteractions(qualifiedNameImporterGenerator)
  }
}
