package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermSelectImporterMatcher
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class TermApplyUnqualifierImplTest extends UnitTestSuite {

  private val termSelectImporterMatcher = mock[TermSelectImporterMatcher]

  private val termApplyUnqualifier = new TermApplyUnqualifierImpl(termSelectImporterMatcher)

  test("unqualify() for 'java.util.List.of()' when matching importer exists should return 'of()'") {
    val matchingImporter = importer"java.util.List.of"

    val importers = List(
      importer"a.b.c",
      matchingImporter
    )

    doReturn(Some(matchingImporter)).when(termSelectImporterMatcher).findMatch(eqTree(q"java.util.List.of"), eqTree(matchingImporter))
    termApplyUnqualifier.unqualify(q"java.util.List.of()", importers).structure shouldBe q"of()".structure
  }

  test("unqualify() for 'java.util.Optional.empty()' when matching importer exists should return 'empty()'") {
    val matchingImporter = importer"java.util.Optional.empty"

    val importers = List(
      importer"a.b.c",
      matchingImporter
    )

    doReturn(Some(matchingImporter)).when(termSelectImporterMatcher).findMatch(eqTree(q"java.util.Optional.empty"), eqTree(matchingImporter))
    termApplyUnqualifier.unqualify(q"java.util.Optional.empty()", importers).structure shouldBe q"empty()".structure
  }

  test("unqualify() for 'foo()' when has matching importer should return unchanged") {
    val importers = List(
      importer"a.foo",
      importer"b.goo"
    )

    termApplyUnqualifier.unqualify(q"foo()", importers).structure shouldBe q"foo()".structure
  }

  test("unqualify() for 'foo()' when has no matching importer should return unchanged") {
    val importers = List(
      importer"b.goo"
    )

    termApplyUnqualifier.unqualify(q"foo()", importers).structure shouldBe q"foo()".structure
  }
}
