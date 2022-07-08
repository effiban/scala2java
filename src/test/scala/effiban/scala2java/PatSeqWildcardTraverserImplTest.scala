package effiban.scala2java

import scala.meta.Pat

class PatSeqWildcardTraverserImplTest extends UnitTestSuite {

  val patSeqWildcardTraverser = new PatSeqWildcardTraverserImpl()

  test("traverse()") {
    patSeqWildcardTraverser.traverse(Pat.SeqWildcard())

    outputWriter.toString shouldBe "/* ... */"
  }

}
