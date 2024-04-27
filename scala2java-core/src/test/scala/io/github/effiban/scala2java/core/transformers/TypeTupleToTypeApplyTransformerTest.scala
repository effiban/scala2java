package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.transformers.TypeTupleToTypeApplyTransformer.transform

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeTupleToTypeApplyTransformerTest extends UnitTestSuite {

  test("transform when 2 args should return java.util.Map.Entry[...]") {
    val args = List(TypeNames.String, TypeNames.Int)
    val expectedTypeApply = Type.Apply(t"java.util.Map#Entry", args)

    transform(Type.Tuple(args)).structure shouldBe expectedTypeApply.structure
  }

  test("transform when 3 args should return org.jooq.lambda.tuple.Tuple3[...]") {
    val args = List(TypeNames.String, TypeNames.Int, TypeNames.Double)
    val expectedTypeApply = Type.Apply(t"org.jooq.lambda.tuple.Tuple3", args)

    transform(Type.Tuple(args)).structure shouldBe expectedTypeApply.structure
  }

  test("transform when 4 args should return org.jooq.lambda.tuple.Tuple4[...]") {
    val args = List(
      TypeNames.String,
      TypeNames.Int,
      TypeNames.Double,
      TypeNames.Float
    )
    val expectedTypeApply = Type.Apply(t"org.jooq.lambda.tuple.Tuple4", args)

    transform(Type.Tuple(args)).structure shouldBe expectedTypeApply.structure
  }
}
