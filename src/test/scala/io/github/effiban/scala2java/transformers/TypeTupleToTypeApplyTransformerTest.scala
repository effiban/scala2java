package io.github.effiban.scala2java.transformers

import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TypeNames
import io.github.effiban.scala2java.transformers.TypeTupleToTypeApplyTransformer.transform

import scala.meta.Type

class TypeTupleToTypeApplyTransformerTest extends UnitTestSuite {

  test("transform when 2 args should return Map.Entry[...]") {
    val args = List(TypeNames.String, TypeNames.Int)
    val expectedTypeApply = Type.Apply(Type.Project(Type.Name("Map"), Type.Name("Entry")), args)

    transform(Type.Tuple(args)).structure shouldBe expectedTypeApply.structure
  }

  test("transform when 3 args should return Tuple3[...]") {
    val args = List(TypeNames.String, TypeNames.Int, TypeNames.Double)
    val expectedTypeApply = Type.Apply(Type.Name("Tuple3"), args)

    transform(Type.Tuple(args)).structure shouldBe expectedTypeApply.structure
  }

  test("transform when 4 args should return Tuple4[...]") {
    val args = List(
      TypeNames.String,
      TypeNames.Int,
      TypeNames.Double,
      TypeNames.Float
    )
    val expectedTypeApply = Type.Apply(Type.Name("Tuple4"), args)

    transform(Type.Tuple(args)).structure shouldBe expectedTypeApply.structure
  }
}
