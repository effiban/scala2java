package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.core.qualifiers.CoreTypeNameQualifier.qualify
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Type, XtensionQuasiquoteType}

class CoreTypeNameQualifierTest extends UnitTestSuite {

  // Please keep in alphabetical order
  private final val PositiveScenarios = Table(
    ("Type", "QualifiedType"),
    (t"AbstractMethodError", ScalaAbstractMethodError),
    (t"Any", ScalaAny),
    (t"AnyRef", ScalaAnyRef),
    (t"AnyVal", ScalaAnyVal),
    (t"Array", ScalaArray),
    (t"ArrayIndexOutOfBoundsException", ScalaArrayIndexOutOfBoundsException),
    (t"BigDecimal", ScalaBigDecimal),
    (t"BigInt", ScalaBigInt),
    (t"Boolean", ScalaBoolean),
    (t"Byte", ScalaByte),
    (t"Char", ScalaChar),
    (t"Class", ScalaClass),
    (t"ClassCastException", ScalaClassCastException),
    (t"Cloneable", ScalaCloneable),
    (t"Double", ScalaDouble),
    (t"Either", ScalaEither),
    (t"Enumeration", ScalaEnumeration),
    (t"Equiv", ScalaEquiv),
    (t"Error", ScalaError),
    (t"Exception", ScalaException),
    (t"Float", ScalaFloat),
    (t"Function", ScalaFunction),
    (t"Fractional", ScalaFractional),
    (t"IllegalArgumentException", ScalaIllegalArgumentException),
    (t"IllegalStateException", t"java.lang.IllegalStateException"),
    (t"Int", ScalaInt),
    (t"IndexedSeq", ScalaIndexedSeq),
    (t"IndexOutOfBoundsException", ScalaIndexOutOfBoundsException),
    (t"Integral", ScalaIntegral),
    (t"InterruptedException", ScalaInterruptedException),
    (t"Iterable", ScalaIterable),
    (t"Iterator", ScalaIterator),
    (t"LazyList", ScalaLazyList),
    (t"Left", ScalaLeft),
    (t"List", ScalaList),
    (t"Long", ScalaLong),
    (t"Map", ScalaMap),
    (t"NoSuchElementException", ScalaNoSuchElementException),
    (t"NullPointerException", ScalaNullPointerException),
    (t"NumberFormatException", ScalaNumberFormatException),
    (t"Numeric", ScalaNumeric),
    (t"Option", ScalaOption),
    (t"Ordered", ScalaOrdered),
    (t"Ordering", ScalaOrdering),
    (t"PartiallyOrdered", ScalaPartiallyOrdered),
    (t"PartialOrdering", ScalaPartialOrdering),
    (t"Range", ScalaRange),
    (t"Right", ScalaRight),
    (t"RuntimeException", ScalaRuntimeException),
    (t"Seq", ScalaSeq),
    (t"Serializable", ScalaSerializable),
    (t"Set", ScalaSet),
    (t"Short", ScalaShort),
    (t"Some", ScalaSome),
    (t"Stream", ScalaStream),
    (t"String", ScalaString),
    (t"StringBuilder", ScalaStringBuilder),
    (t"StringIndexOutOfBoundsException", ScalaStringIndexOutOfBoundsException),
    (t"Throwable", ScalaThrowable),
    (t"Unit", ScalaUnit),
    (t"UnsupportedOperationException", ScalaUnsupportedOperationException),
    (t"Vector", ScalaVector)
  )

  forAll(PositiveScenarios) { (tpe: Type.Name, expectedQualifiedType: Type.Select) =>
    test(s"qualified type of $tpe should be $expectedQualifiedType") {
      qualify(tpe).value.structure shouldBe expectedQualifiedType.structure
    }
  }

  test("qualify() when unmapped should return None") {
    qualify(t"Bla") shouldBe None
  }
}
