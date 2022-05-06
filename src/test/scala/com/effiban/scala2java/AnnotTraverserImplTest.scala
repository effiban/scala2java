package com.effiban.scala2java

import org.mockito.ArgumentMatchers.any

import scala.meta.Mod.Annot
import scala.meta.{Init, Name, Type}

class AnnotTraverserImplTest extends UnitTestSuite {

  private val initTraverser = mock[InitTraverser]

  private val annotTraverser = new AnnotTraverserImpl(initTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()
    doAnswer((init: Init) => outputWriter.write(init.toString())).when(initTraverser).traverse(any[Init])
  }

  test("traverse") {
    annotTraverser.traverse(Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List())))

    outputWriter.toString shouldBe "@MyAnnot1"
  }
}
