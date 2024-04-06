package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TermSelects._
import io.github.effiban.scala2java.core.qualifiers.CoreTermNameQualifier.qualify
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, XtensionQuasiquoteTerm}

class CoreTermNameQualifierTest extends UnitTestSuite {

  // Please keep in alphabetical order
  private final val PositiveScenarios = Table(
    ("Term", "QualifiedTerm"),
    (q"Array", ScalaArray),
    (q"BigDecimal", ScalaBigDecimal),
    (q"BigInt", ScalaBigInt),
    (q"Boolean", ScalaBoolean),
    (q"Byte", ScalaByte),
    (q"Char", ScalaChar),
    (q"Double", ScalaDouble),
    (q"Either", ScalaEither),
    (q"Equiv", ScalaEquiv),
    (q"Float", ScalaFloat),
    (q"Function", ScalaFunction),
    (q"Fractional", ScalaFractional),
    (q"IllegalArgumentException", JavaIllegalArgumentException),
    (q"IllegalStateException", JavaIllegalStateException),
    (q"Int", ScalaInt),
    (q"IndexedSeq", ScalaIndexedSeq),
    (q"Integral", ScalaIntegral),
    (q"Iterable", ScalaIterable),
    (q"Iterator", ScalaIterator),
    (q"LazyList", ScalaLazyList),
    (q"Left", ScalaLeft),
    (q"List", ScalaList),
    (q"Long", ScalaLong),
    (q"Map", ScalaMap),
    (q"Nil", ScalaNil),
    (q"None", ScalaNone),
    (q"Numeric", ScalaNumeric),
    (q"Option", ScalaOption),
    (q"Ordered", ScalaOrdered),
    (q"Ordering", ScalaOrdering),
    (q"print", ScalaPrint),
    (q"printf", ScalaPrintf),
    (q"println", ScalaPrintln),
    (q"Range", ScalaRange),
    (q"Right", ScalaRight),
    (q"Seq", ScalaSeq),
    (q"Set", ScalaSet),
    (q"Short", ScalaShort),
    (q"Some", ScalaSome),
    (q"Stream", ScalaStream),
    (q"StringBuilder", ScalaStringBuilder),
    (q"Vector", ScalaVector)
  )

  forAll(PositiveScenarios) { (termName: Term.Name, expectedQualifiedTerm: Term.Select) =>
    test(s"qualified term of $termName should be $expectedQualifiedTerm") {
      qualify(termName).value.structure shouldBe expectedQualifiedTerm.structure
    }
  }

  test("qualify() when unmapped should return None") {
    qualify(q"Bla") shouldBe None
  }
}
