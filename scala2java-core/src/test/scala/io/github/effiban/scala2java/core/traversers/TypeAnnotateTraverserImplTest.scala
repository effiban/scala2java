package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteMod, XtensionQuasiquoteType}

class TypeAnnotateTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeAnnotateTraverser = new TypeAnnotateTraverserImpl(typeTraverser)

  test("traverse") {

    val annot1 = mod"@MyAnnot1"
    val annot2 = mod"@MyAnnot2"
    val annots = List(annot1, annot2)

    val tpe = t"T"
    val traversedType = t"U"

    val typeAnnotate = Type.Annotate(tpe = tpe, annots = annots)
    val traversedTypeAnnotate = Type.Annotate(tpe = traversedType, annots = annots)

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))

    typeAnnotateTraverser.traverse(typeAnnotate).structure shouldBe traversedTypeAnnotate.structure
  }

}
