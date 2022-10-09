package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TypeNames

import scala.meta.Type

class TypeApplyTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val typeListTraverser = mock[TypeListTraverser]

  private val typeApplyTraverser = new TypeApplyTraverserImpl(typeTraverser, typeListTraverser)

  test("traverse() a 'Map'") {
    val tpe = TypeNames.Map
    val args = List(TypeNames.Int, TypeNames.String)

    val typeApply = Type.Apply(tpe = tpe, args = args)

    doWrite("Map").when(typeTraverser).traverse(eqTree(tpe))
    doWrite("<Integer, String>").when(typeListTraverser).traverse(eqTreeList(args))

    typeApplyTraverser.traverse(typeApply)

    outputWriter.toString shouldBe "Map<Integer, String>"
  }

  test("traverse() a valid 'Array'") {
    val tpe = TypeNames.ScalaArray
    val args = List(TypeNames.String)

    val typeApply = Type.Apply(tpe = tpe, args = args)

    doWrite("String").when(typeTraverser).traverse(eqTree(TypeNames.String))

    typeApplyTraverser.traverse(typeApply)

    outputWriter.toString shouldBe "String[]"
  }

  test("traverse() an 'Array' with 2 type args should throw an exception") {
    val tpe = TypeNames.ScalaArray
    val args = List(TypeNames.String, TypeNames.Int)

    val typeApply = Type.Apply(tpe = tpe, args = args)

    intercept[IllegalStateException] {
      typeApplyTraverser.traverse(typeApply)
    }
  }
}
