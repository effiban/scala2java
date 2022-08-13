package effiban.scala2java.typeinference

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Lit, Type}

class LitTypeInferrerTest extends UnitTestSuite {

  private val LiteralTypeMappings = Table(
    ("Literal", "MaybeExpectedTypeValue"),
    (Lit.Boolean(true), Some("Boolean")),
    (Lit.Byte(1), Some("Byte")),
    (Lit.Short(1), Some("Short")),
    (Lit.Int(1), Some("Int")),
    (Lit.Long(1), Some("Long")),
    (Lit.Float(1.1f), Some("Float")),
    (Lit.Double(1.1), Some("Double")),
    (Lit.Char('a'), Some("Char")),
    (Lit.String("abc"), Some("String")),
    (Lit.Unit(), Some("Unit")),
    (Lit.Null(), None),
    (Lit.Symbol(scala.Symbol("sym")), None)
  )

  forAll(LiteralTypeMappings) {
    (literal: Lit, maybeExpectedTypeValue: Option[String]) => {
      val maybeExpectedTypeName = maybeExpectedTypeValue.map(Type.Name(_))
      test(s"Infer $literal should return $maybeExpectedTypeName") {
        LitTypeInferrer.infer(literal).structure shouldBe maybeExpectedTypeName.structure
      }
    }
  }
}
