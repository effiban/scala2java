package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Mod.Annot
import scala.meta.{Init, Name, Type, XtensionQuasiquoteTerm}

class AnnotTraverserImplTest extends UnitTestSuite {

  private val initTraverser = mock[InitTraverser]

  private val annotTraverser = new AnnotTraverserImpl(initTraverser)

  test("traverse") {
    val init = Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(),
      argss = List(List(q"arg1", q"arg2"))
    )
    val traversedInit = Init(tpe = Type.Name("MyAnnot11"), name = Name.Anonymous(),
      argss = List(List(q"arg11", q"arg22"))
    )

    doReturn(traversedInit).when(initTraverser).traverse(eqTree(init))

    annotTraverser.traverse(Annot(init)).structure shouldBe Annot(traversedInit).structure
  }
}
