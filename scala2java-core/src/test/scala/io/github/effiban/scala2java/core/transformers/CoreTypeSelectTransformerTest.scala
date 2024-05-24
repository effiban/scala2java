package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.CoreTypeSelectTransformer.transform

import scala.meta.{Type, XtensionQuasiquoteType}

class CoreTypeSelectTransformerTest extends UnitTestSuite {

  // Please keep in alphabetical order
  private val SupportedTypeMappings = Table(
    ("ScalaType", "ExpectedJavaType"),
    (ScalaAbstractMethodError, JavaAbstractMethodError),
    (ScalaAny, JavaObject),
    (ScalaAnyRef, JavaObject),
    (ScalaAnyVal, JavaObject),
    (ScalaArrayIndexOutOfBoundsException, JavaArrayIndexOutOfBoundsException),
    (ScalaBigDecimal, JavaBigDecimal),
    (ScalaBigInt, JavaBigInt),
    (ScalaBoolean, t"boolean"),
    (ScalaByte, t"byte"),
    (ScalaChar, t"char"),
    (ScalaClass, JavaClass),
    (ScalaClassCastException, JavaClassCastException),
    (ScalaCloneable, JavaCloneable),
    (ScalaDouble, t"double"),
    (ScalaEither, JavaEither),
    (ScalaError, JavaError),
    (ScalaException, JavaException),
    (ScalaFailure, JavaTry),
    (ScalaFloat, t"float"),
    (ScalaFunction, JavaFunction),
    (ScalaFuture, JavaCompletableFuture),
    (ScalaIllegalArgumentException, JavaIllegalArgumentException),
    (ScalaIndexedSeq, JavaList),
    (ScalaIndexOutOfBoundsException, JavaIndexOutOfBoundsException),
    (ScalaInt, t"int"),
    (ScalaInterruptedException, JavaInterruptedException),
    (ScalaIterable, JavaIterable),
    (ScalaIterator, JavaIterator),
    (ScalaLazyList, JavaStream),
    (ScalaLeft, JavaEither),
    (ScalaList, JavaList),
    (ScalaLong, t"long"),
    (ScalaMap, JavaMap),
    (ScalaNoSuchElementException, JavaNoSuchElementException),
    (ScalaNullPointerException, JavaNullPointerException),
    (ScalaNumberFormatException, JavaNumberFormatException),
    (ScalaOption, JavaOptional),
    (ScalaRange, t"java.util.List[java.lang.Integer]"),
    (ScalaRight, JavaEither),
    (ScalaRuntimeException, JavaRuntimeException),
    (ScalaSeq, JavaList),
    (ScalaSerializable, JavaSerializable),
    (ScalaSet, JavaSet),
    (ScalaShort, t"short"),
    (ScalaSome, JavaOptional),
    (ScalaStream, JavaStream),
    (ScalaString, JavaString),
    (ScalaStringIndexOutOfBoundsException, JavaStringIndexOutOfBoundsException),
    (ScalaStringBuilder, JavaStringBuilder),
    (ScalaSuccess, JavaTry),
    (ScalaThrowable, JavaThrowable),
    (ScalaTry, JavaTry),
    (ScalaUnit, t"void"),
    (ScalaUnsupportedOperationException, JavaUnsupportedOperationException),
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
