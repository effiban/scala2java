package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TypeProjectImporterGeneratorTest extends UnitTestSuite {

  private val typeToTermRefConverter = mock[TypeToTermRefConverter]

  private val typeProjectImporterGenerator = new TypeProjectImporterGeneratorImpl(typeToTermRefConverter)

  test("generate when converter is able to convert the qualifier, should return the corresponding importer") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"A"))).thenReturn(Some(q"A"))

    typeProjectImporterGenerator.generate(t"A#B").value.structure shouldBe importer"A.B".structure
  }

  test("generate when converter cannot convert the qualifier, should return None") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"A[T]"))).thenReturn(None)

    typeProjectImporterGenerator.generate(t"A[T]#B") shouldBe None
  }
}
