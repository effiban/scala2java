package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Lit, Type}

class LitTypeInferrerTest extends UnitTestSuite {

  private val LiteralTypeMappings = Table(
    ("Literal", "ExpectedType"),
    (Lit.Boolean(true), ScalaBoolean),
    (Lit.Byte(1), ScalaByte),
    (Lit.Short(1), ScalaShort),
    (Lit.Int(1), ScalaInt),
    (Lit.Long(1), ScalaLong),
    (Lit.Float(1.1f), ScalaFloat),
    (Lit.Double(1.1), ScalaDouble),
    (Lit.Char('a'), ScalaChar),
    (Lit.String("abc"), ScalaString),
    (Lit.Symbol(scala.Symbol("sym")), ScalaString),
    (Lit.Unit(), ScalaUnit),
    (Lit.Null(), Type.AnonymousName())
  )

  forAll(LiteralTypeMappings) {
    (literal: Lit, expectedType: Type) => {
      test(s"Infer $literal should return ${typeToString(expectedType)}") {
        LitTypeInferrer.infer(literal).value.structure shouldBe expectedType.structure
      }
    }
  }

  private def typeToString(tpe: Type) = {
    tpe match {
      case Type.AnonymousName() => "<<anonymous>>"
      case t => t.toString()
    }
  }
}
