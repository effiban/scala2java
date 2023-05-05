package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.testtrees.TypeNames.ScalaArray
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeApplyTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeApplyTraverser = new TypeApplyTraverserImpl(typeTraverser)

  test("traverse() an arbitrary type") {
    val tpe = t"X"
    val traversedType = t"Y"

    val typeArg1 = t"T1"
    val typeArg2 = t"T2"
    val typeArgs = List(typeArg1, typeArg2)

    val traversedTypeArg1 = t"U1"
    val traversedTypeArg2 = t"U2"
    val traversedTypeArgs = List(traversedTypeArg1, traversedTypeArg2)

    val typeApply = Type.Apply(tpe = tpe, args = typeArgs)
    val traversedTypeApply = Type.Apply(tpe = traversedType, args = traversedTypeArgs)

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))
    doReturn(traversedTypeArg1).when(typeTraverser).traverse(eqTree(typeArg1))
    doReturn(traversedTypeArg2).when(typeTraverser).traverse(eqTree(typeArg2))

    typeApplyTraverser.traverse(typeApply).structure shouldBe traversedTypeApply.structure
  }

  test("traverse() a valid 'Array'") {
    val tpe = ScalaArray
    val typeArg = t"T"
    val traversedTypeArg = t"U"

    val typeApply = Type.Apply(tpe = tpe, args = List(typeArg))
    val traversedTypeApply = Type.Apply(tpe = tpe, args = List(traversedTypeArg))

    doReturn(traversedTypeArg).when(typeTraverser).traverse(eqTree(typeArg))

    typeApplyTraverser.traverse(typeApply).structure shouldBe traversedTypeApply.structure
  }

  test("traverse() an 'Array' with 2 type args should throw an exception") {
    val tpe = ScalaArray
    val args = List(TypeNames.String, TypeNames.Int)

    val typeApply = Type.Apply(tpe = tpe, args = args)

    intercept[IllegalStateException] {
      typeApplyTraverser.traverse(typeApply)
    }
  }
}
