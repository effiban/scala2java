package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class TermApplyImporterGeneratorTest extends UnitTestSuite {

  private val qualifiedNameImporterGenerator = mock[QualifiedNameImporterGenerator]

  private val termApplyImporterGenerator = new TermApplyImporterGeneratorImpl(qualifiedNameImporterGenerator)

  test("generate() for 'A.b(3)' when identified by the inner generator as a Java static method call, should return the result of the inner generator") {
    val importer = importer"A.b"
    when(qualifiedNameImporterGenerator.generateForStaticMethod(eqTree(q"A"), eqTo("b"), eqTreeList(List(q"3")))).thenReturn(Some(importer))
    termApplyImporterGenerator.generate(q"A.b(3)").value.structure shouldBe importer.structure
  }

  test("generate() for 'A.B.c(3)' when identified by the inner generator as a Java static method call, should return the result of the inner generator") {
    val importer = importer"A.B.c"
    when(qualifiedNameImporterGenerator.generateForStaticMethod(eqTree(q"A.B"), eqTo("c"), eqTreeList(List(q"3")))).thenReturn(Some(importer))
    termApplyImporterGenerator.generate(q"A.B.c(3)").value.structure shouldBe importer.structure
  }

  test("generate() for 'A.b(3)' when not identified by the inner generator as a Java static method call, should return None") {
    when(qualifiedNameImporterGenerator.generateForStaticMethod(eqTree(q"A"), eqTo("b"), eqTreeList(List(q"3")))).thenReturn(None)
    termApplyImporterGenerator.generate(q"A.b(3)") shouldBe None
  }

  test("generate() for 'A.b(3).C.D(4)' should not call the inner generator and should return None") {
    termApplyImporterGenerator.generate(q"A.b(3).C.D(4)") shouldBe None

    verifyNoMoreInteractions(qualifiedNameImporterGenerator)
  }

  test("generate() for 'func(2)' should not call the inner generator and should return None") {
    termApplyImporterGenerator.generate(q"func(2)") shouldBe None

    verifyNoMoreInteractions(qualifiedNameImporterGenerator)
  }

  test("generate() for 'scala.Array.apply(2)' should not call the inner generator and should return None") {
    termApplyImporterGenerator.generate(q"scala.Array.apply(2)") shouldBe None

    verifyNoMoreInteractions(qualifiedNameImporterGenerator)
  }
}
