package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Pat

class PatSeqWildcardTraverserImplTest extends UnitTestSuite {

  val patSeqWildcardTraverser = new PatSeqWildcardTraverserImpl()

  test("traverse()") {
    patSeqWildcardTraverser.traverse(Pat.SeqWildcard())

    outputWriter.toString shouldBe "/* ... */"
  }

}
