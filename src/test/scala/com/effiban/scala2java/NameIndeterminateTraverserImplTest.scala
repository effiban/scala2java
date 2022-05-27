package com.effiban.scala2java

import scala.meta.Name

class NameIndeterminateTraverserImplTest extends UnitTestSuite {

  val nameIndeterminateTraverser = new NameIndeterminateTraverserImpl()

  test("traverse()") {
    nameIndeterminateTraverser.traverse(Name.Indeterminate("myName"))
    outputWriter.toString shouldBe "myName"
  }
}
