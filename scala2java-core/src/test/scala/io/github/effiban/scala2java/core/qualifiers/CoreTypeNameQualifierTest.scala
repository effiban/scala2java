package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.core.qualifiers.CoreTypeNameQualifier.qualify
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Type, XtensionQuasiquoteType}

class CoreTypeNameQualifierTest extends UnitTestSuite {

  // Please keep in alphabetical order
  private final val PositiveScenarios = Table(
    ("Type", "QualifiedType"),
    (t"AbstractMethodError", JavaAbstractMethodError),
    (t"Any", ScalaAny),
    (t"AnyRef", JavaObject),
    (t"AnyVal", ScalaAnyVal),
    (t"Array", ScalaArray),
    (t"ArrayIndexOutOfBoundsException", JavaArrayIndexOutOfBoundsException),
    (t"BigDecimal", ScalaBigDecimal),
    (t"BigInt", ScalaBigInt),
    (t"Boolean", ScalaBoolean),
    (t"Byte", ScalaByte),
    (t"Char", ScalaChar),
    (t"Class", JavaClass),
    (t"ClassCastException", JavaClassCastException),
    (t"Cloneable", JavaCloneable),
    (t"Double", ScalaDouble),
    (t"Either", ScalaEither),
    (t"Enumeration", ScalaEnumeration),
    (t"Equiv", ScalaEquiv),
    (t"Error", JavaError),
    (t"Exception", JavaException),
    (t"Float", ScalaFloat),
    (t"Function", ScalaFunction1),
    (t"Function0", ScalaFunction0),
    (t"Function1", ScalaFunction1),
    (t"Function2", ScalaFunction2),
    (t"Function3", ScalaFunction3),
    (t"Function4", ScalaFunction4),
    (t"Function5", ScalaFunction5),
    (t"Function6", ScalaFunction6),
    (t"Function7", ScalaFunction7),
    (t"Function8", ScalaFunction8),
    (t"Function9", ScalaFunction9),
    (t"Function10", ScalaFunction10),
    (t"Function11", ScalaFunction11),
    (t"Function12", ScalaFunction12),
    (t"Function13", ScalaFunction13),
    (t"Function14", ScalaFunction14),
    (t"Function15", ScalaFunction15),
    (t"Function16", ScalaFunction16),
    (t"Function17", ScalaFunction17),
    (t"Function18", ScalaFunction18),
    (t"Function19", ScalaFunction19),
    (t"Function20", ScalaFunction20),
    (t"Function21", ScalaFunction21),
    (t"Function22", ScalaFunction22),
    (t"Fractional", ScalaFractional),
    (t"IllegalArgumentException", JavaIllegalArgumentException),
    (t"IllegalStateException", JavaIllegalStateException),
    (t"Int", ScalaInt),
    (t"IndexedSeq", ScalaIndexedSeq),
    (t"IndexOutOfBoundsException", JavaIndexOutOfBoundsException),
    (t"Integral", ScalaIntegral),
    (t"InterruptedException", JavaInterruptedException),
    (t"Iterable", ScalaIterable),
    (t"Iterator", ScalaIterator),
    (t"LazyList", ScalaLazyList),
    (t"Left", ScalaLeft),
    (t"List", ScalaList),
    (t"Long", ScalaLong),
    (t"Map", ScalaMap),
    (t"NoSuchElementException", JavaNoSuchElementException),
    (t"NullPointerException", JavaNullPointerException),
    (t"NumberFormatException", JavaNumberFormatException),
    (t"Numeric", ScalaNumeric),
    (t"Option", ScalaOption),
    (t"Ordered", ScalaOrdered),
    (t"Ordering", ScalaOrdering),
    (t"PartiallyOrdered", ScalaPartiallyOrdered),
    (t"PartialOrdering", ScalaPartialOrdering),
    (t"Range", ScalaRange),
    (t"Right", ScalaRight),
    (t"RuntimeException", JavaRuntimeException),
    (t"Seq", ScalaSeq),
    (t"Serializable", JavaSerializable),
    (t"Set", ScalaSet),
    (t"Short", ScalaShort),
    (t"Some", ScalaSome),
    (t"Stream", ScalaStream),
    (t"String", JavaString),
    (t"StringBuilder", ScalaStringBuilder),
    (t"StringIndexOutOfBoundsException", JavaStringIndexOutOfBoundsException),
    (t"Throwable", JavaThrowable),
    (t"Unit", ScalaUnit),
    (t"UnsupportedOperationException", JavaUnsupportedOperationException),
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
