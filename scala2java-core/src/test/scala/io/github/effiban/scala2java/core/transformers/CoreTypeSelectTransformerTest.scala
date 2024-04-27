package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TypeNames
import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.CoreTypeSelectTransformer.transform

import scala.meta.{Type, XtensionQuasiquoteType}

class CoreTypeSelectTransformerTest extends UnitTestSuite {

  // Please keep in alphabetical order
  private val SupportedTypeMappings = Table(
    ("ScalaType", "ExpectedJavaType"),
    (ScalaAbstractMethodError, t"AbstractMethodError"),
    (ScalaAny, t"Object"),
    (ScalaAnyRef, t"Object"),
    (ScalaAnyVal, t"Object"),
    (ScalaArrayIndexOutOfBoundsException, t"ArrayIndexOutOfBoundsException"),
    (ScalaBigDecimal, JavaBigDecimal),
    (ScalaBigInt, JavaBigInt),
    (ScalaBoolean, t"boolean"),
    (ScalaByte, t"byte"),
    (ScalaChar, t"char"),
    (ScalaClass, TypeNames.Class),
    (ScalaClassCastException, t"ClassCastException"),
    (ScalaCloneable, t"Cloneable"),
    (ScalaDouble, t"double"),
    (ScalaEither, JavaEither),
    (ScalaError, t"Error"),
    (ScalaException, t"Exception"),
    (ScalaFailure, JavaTry),
    (ScalaFloat, t"float"),
    (ScalaFunction, JavaFunction),
    (ScalaFuture, JavaCompletableFuture),
    (ScalaIllegalArgumentException, t"IllegalArgumentException"),
    (ScalaIndexedSeq, JavaList),
    (ScalaIndexOutOfBoundsException, t"IndexOutOfBoundsException"),
    (ScalaInt, t"int"),
    (ScalaInterruptedException, t"InterruptedException"),
    (ScalaIterable, t"Iterable"),
    (ScalaIterator, t"Iterator"),
    (ScalaLazyList, JavaStream),
    (ScalaLeft, JavaEither),
    (ScalaList, JavaList),
    (ScalaLong, t"long"),
    (ScalaMap, JavaMap),
    (ScalaNoSuchElementException, JavaNoSuchElementException),
    (ScalaNullPointerException, t"NullPointerException"),
    (ScalaNumberFormatException, t"NumberFormatException"),
    (ScalaOption, JavaOptional),
    (ScalaRange, t"java.util.List[Integer]"),
    (ScalaRight, JavaEither),
    (ScalaRuntimeException, t"RuntimeException"),
    (ScalaSeq, JavaList),
    (ScalaSerializable, JavaSerializable),
    (ScalaSet, JavaSet),
    (ScalaShort, t"short"),
    (ScalaSome, JavaOptional),
    (ScalaStream, JavaStream),
    (ScalaString, t"String"),
    (ScalaStringIndexOutOfBoundsException, t"StringIndexOutOfBoundsException"),
    (ScalaStringBuilder, t"StringBuilder"),
    (ScalaSuccess, JavaTry),
    (ScalaThrowable, t"Throwable"),
    (ScalaTry -> JavaTry),
    (ScalaUnit, t"void"),
    (ScalaUnsupportedOperationException, t"UnsupportedOperationException"),
    (ScalaVector, JavaList),
  )

  forAll(SupportedTypeMappings) { (scalaType: Type.Select, expectedJavaType: Type) =>
    test(s"transform $scalaType should return $expectedJavaType") {
      transform(scalaType).value.structure shouldBe expectedJavaType.structure
    }
  }

  test("transform() when unsupported should return None") {
    transform(t"a.b.C") shouldBe None
  }
}
