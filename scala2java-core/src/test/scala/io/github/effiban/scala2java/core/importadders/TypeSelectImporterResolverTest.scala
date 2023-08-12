package io.github.effiban.scala2java.core.importadders

import io.github.effiban.scala2java.core.importadders.TypeSelectImporterResolver.resolve
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TypeSelectImporterResolverTest extends UnitTestSuite {

  test("resolve") {
    resolve(t"a.b.C").structure shouldBe importer"a.b.C".structure
  }
}
