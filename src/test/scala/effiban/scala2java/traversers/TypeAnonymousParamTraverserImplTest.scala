package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Mod, Type}

class TypeAnonymousParamTraverserImplTest extends UnitTestSuite {

  private val typeAnonymousParamTraverser = new TypeAnonymousParamTraverserImpl()

  test("traverse") {
    typeAnonymousParamTraverser.traverse(Type.AnonymousParam(Some(Mod.Covariant())))

    outputWriter.toString shouldBe "?"
  }

}
