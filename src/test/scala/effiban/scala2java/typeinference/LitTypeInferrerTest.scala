package effiban.scala2java.typeinference

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.{Lit, Type}

class LitTypeInferrerTest extends UnitTestSuite {

  private val LiteralTypeMappings = Table(
    ("Literal", "ExpectedType"),
    (Lit.Boolean(true), Type.Name("Boolean")),
    (Lit.Byte(1), Type.Name("Byte")),
    (Lit.Short(1), Type.Name("Short")),
    (Lit.Int(1), Type.Name("Int")),
    (Lit.Long(1), Type.Name("Long")),
    (Lit.Float(1.1f), Type.Name("Float")),
    (Lit.Double(1.1), Type.Name("Double")),
    (Lit.Char('a'), Type.Name("Char")),
    (Lit.String("abc"), TypeNames.String),
    (Lit.Symbol(scala.Symbol("sym")), TypeNames.String),
    (Lit.Unit(), Type.Name("Unit")),
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
