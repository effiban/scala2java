package com.effiban.scala2java

import org.mockito.ArgumentMatchers.any

import scala.meta.{Lit, Pat}

class AlternativeTraverserImplTest extends UnitTestSuite {

  private val patTraverser = mock[PatTraverser]

  private val alternativeTraverser = new AlternativeTraverserImpl(patTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()
    doAnswer((`int`: Lit.Int) => outputWriter.write(`int`.value.toString)).when(patTraverser).traverse(any[Lit.Int])
  }

  test("traverse") {
    alternativeTraverser.traverse(Pat.Alternative(Lit.Int(3), Lit.Int(4)))

    outputWriter.toString shouldBe "3, 4"
  }
}
