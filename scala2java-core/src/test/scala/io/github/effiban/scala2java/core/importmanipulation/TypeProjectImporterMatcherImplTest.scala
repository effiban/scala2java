package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TypeProjectImporterMatcherImplTest extends UnitTestSuite {

  private val typeToTermRefConverter = mock[TypeToTermRefConverter]
  private val typeProjectImporterMatcher = new TypeProjectImporterMatcherImpl(typeToTermRefConverter)

  test(s"Type 'A#B' should match importer 'A.B'") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"A"))).thenReturn(Some(q"A"))
    typeProjectImporterMatcher.findMatch(t"A#B", importer"A.B").value.structure shouldBe importer"A.B".structure
  }

  test(s"Type 'a.B#C' should match importer 'a.B.C'") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"a.B"))).thenReturn(Some(q"a.B"))
    typeProjectImporterMatcher.findMatch(t"a.B#C", importer"a.B.C").value.structure shouldBe importer"a.B.C".structure
  }

  test(s"Type 'a.B#C' should match importer 'a.B._'") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"a.B"))).thenReturn(Some(q"a.B"))
    typeProjectImporterMatcher.findMatch(t"a.B#C", importer"a.B._").value.structure shouldBe importer"a.B._".structure
  }

  test(s"Type 'a.B#C' given importer 'a.B.{C, D}', should match importer 'a.B.C'") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"a.B"))).thenReturn(Some(q"a.B"))
    typeProjectImporterMatcher.findMatch(t"a.B#C", importer"a.B.{C, D}").value.structure shouldBe importer"a.B.C".structure
  }

  test(s"Type 'a.B#C' should not match importer 'e.F.G'") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"a.B"))).thenReturn(Some(q"a.B"))
    typeProjectImporterMatcher.findMatch(t"a.B#C", importer"e.F.G") shouldBe None
  }

  test(s"Type 'a.B#D' should not match importer 'a.C.D'") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"a.B"))).thenReturn(Some(q"a.B"))
    typeProjectImporterMatcher.findMatch(t"a.B#C", importer"a.C.D") shouldBe None
  }

  test(s"Type 'a.B#C' should not match importer 'a.B'") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"a.B"))).thenReturn(Some(q"a.B"))
    typeProjectImporterMatcher.findMatch(t"a.B#C", importer"a.B") shouldBe None
  }

  test(s"Type 'B.C' should not match importer 'a.B.C'") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"B"))).thenReturn(Some(q"B"))
    typeProjectImporterMatcher.findMatch(t"B#C", importer"a.B.C") shouldBe None
  }

  test(s"Type 'A[T]#B' should not match any importer") {
    when(typeToTermRefConverter.toTermRefPath(eqTree(t"A[T]"))).thenReturn(None)
    typeProjectImporterMatcher.findMatch(t"A[T]#B", importer"A.B") shouldBe None
  }
}
