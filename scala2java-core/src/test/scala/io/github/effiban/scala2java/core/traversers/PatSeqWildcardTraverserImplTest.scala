package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Pat

class PatSeqWildcardTraverserImplTest extends UnitTestSuite {

  val patSeqWildcardTraverser = new PatSeqWildcardTraverserImpl()

  test("traverse()") {
    patSeqWildcardTraverser.traverse(Pat.SeqWildcard())

    outputWriter.toString shouldBe "/* ... */"
  }

}
