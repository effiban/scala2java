package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Name

class NameIndeterminateTraverserImplTest extends UnitTestSuite {

  val nameIndeterminateTraverser = new NameIndeterminateTraverserImpl()

  test("traverse()") {
    nameIndeterminateTraverser.traverse(Name.Indeterminate("myName"))
    outputWriter.toString shouldBe "myName"
  }
}
