package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.Selfs

import scala.meta.{Name, Self, Type}

class SelfTraverserImplTest extends UnitTestSuite {

  private val selfTraverser = new SelfTraverserImpl()

  test("traverse when has a type") {
    val `self` = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(Type.Name("SelfType")))

    selfTraverser.traverse(`self`)

    outputWriter.toString shouldBe "/* extends SelfName: SelfType */"
  }

  test("traverse when empty") {
    selfTraverser.traverse(Selfs.Empty)

    outputWriter.toString shouldBe ""
  }
}
