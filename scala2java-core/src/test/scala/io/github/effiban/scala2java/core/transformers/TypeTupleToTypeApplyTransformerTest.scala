package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeTupleToTypeApplyTransformerTest extends UnitTestSuite {

  private val treeTransformer = mock[TreeTransformer]

  private val typeTupleToTypeApplyTransformer = new TypeTupleToTypeApplyTransformerImpl(treeTransformer)

  test("transform when 2 args should return java.util.Map.Entry[...]") {
    val args = List(TypeSelects.ScalaString, TypeSelects.ScalaInt)
    val transformedArgs = List(TypeSelects.JavaString, t"int")
    val expectedTypeApply = Type.Apply(t"java.util.Map#Entry", transformedArgs)

    doAnswer((arg: Type) => arg match {
      case t"scala.Predef.String" => TypeSelects.JavaString
      case t"scala.Int" => t"int"
      case other => other
    }).when(treeTransformer).transform(any[Type])

    typeTupleToTypeApplyTransformer.transform(Type.Tuple(args)).structure shouldBe expectedTypeApply.structure
  }

  test("transform when 3 args should return org.jooq.lambda.tuple.Tuple3[...]") {
    val args = List(TypeSelects.ScalaString, TypeSelects.ScalaInt, TypeSelects.ScalaDouble)
    val transformedArgs = List(TypeSelects.JavaString, t"int", t"double")
    val expectedTypeApply = Type.Apply(t"org.jooq.lambda.tuple.Tuple3", transformedArgs)

    doAnswer((arg: Type) => arg match {
      case t"scala.Predef.String" => TypeSelects.JavaString
      case t"scala.Int" => t"int"
      case t"scala.Double" => t"double"
      case other => other
    }).when(treeTransformer).transform(any[Type])

    typeTupleToTypeApplyTransformer.transform(Type.Tuple(args)).structure shouldBe expectedTypeApply.structure
  }

  test("transform when 4 args should return org.jooq.lambda.tuple.Tuple4[...]") {
    val args = List(
      TypeSelects.ScalaString,
      TypeSelects.ScalaInt,
      TypeSelects.ScalaDouble,
      TypeSelects.ScalaFloat
    )
    val transformedArgs = List(
      TypeSelects.JavaString,
      t"int",
      t"double",
      t"float"
    )

    val expectedTypeApply = Type.Apply(t"org.jooq.lambda.tuple.Tuple4", transformedArgs)

    doAnswer((arg: Type) => arg match {
      case t"scala.Predef.String" => TypeSelects.JavaString
      case t"scala.Int" => t"int"
      case t"scala.Double" => t"double"
      case t"scala.Float" => t"float"
      case other => other
    }).when(treeTransformer).transform(any[Type])

    typeTupleToTypeApplyTransformer.transform(Type.Tuple(args)).structure shouldBe expectedTypeApply.structure
  }
}
