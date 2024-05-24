package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer

import scala.meta.{Type, XtensionQuasiquoteType}

object CoreTypeSelectTransformer extends TypeSelectTransformer {

  // Please keep in alphabetical order
  private final val ScalaTypeToJavaType = Map[Type.Select, Type](
    ScalaAbstractMethodError -> JavaAbstractMethodError,
    ScalaAny -> JavaObject,
    ScalaAnyRef -> JavaObject,
    ScalaAnyVal -> JavaObject,
    ScalaArrayIndexOutOfBoundsException -> JavaArrayIndexOutOfBoundsException,
    ScalaBigDecimal -> JavaBigDecimal,
    ScalaBigInt -> JavaBigInt,
    ScalaBoolean -> t"boolean",
    ScalaByte -> t"byte",
    ScalaChar -> t"char",
    ScalaClass -> JavaClass,
    ScalaClassCastException -> JavaClassCastException,
    ScalaCloneable -> JavaCloneable,
    ScalaDouble -> t"double",
    ScalaEither -> JavaEither,
    ScalaError -> JavaError,
    ScalaException -> JavaException,
    ScalaFailure -> JavaTry,
    ScalaFloat -> t"float",
    ScalaFunction -> JavaFunction,
    ScalaFuture -> JavaCompletableFuture,
    ScalaIllegalArgumentException -> JavaIllegalArgumentException,
    ScalaIndexedSeq -> JavaList,
    ScalaIndexOutOfBoundsException -> JavaIndexOutOfBoundsException,
    ScalaInt -> t"int",
    ScalaInterruptedException -> JavaInterruptedException,
    ScalaIterable -> JavaIterable,
    ScalaIterator -> JavaIterator,
    ScalaLazyList -> JavaStream,
    ScalaLeft -> JavaEither,
    ScalaList -> JavaList,
    ScalaLong -> t"long",
    ScalaMap -> JavaMap,
    ScalaNoSuchElementException -> JavaNoSuchElementException,
    ScalaNullPointerException -> JavaNullPointerException,
    ScalaNumberFormatException -> JavaNumberFormatException,
    ScalaOption -> JavaOptional,
    ScalaRange -> t"java.util.List[java.lang.Integer]",
    ScalaRight -> JavaEither,
    ScalaRuntimeException -> JavaRuntimeException,
    ScalaSeq -> JavaList,
    ScalaSet -> JavaSet,
    ScalaSerializable -> JavaSerializable,
    ScalaShort -> t"short",
    ScalaSome -> JavaOptional,
    ScalaStream -> JavaStream,
    ScalaString -> JavaString,
    ScalaStringBuilder -> JavaStringBuilder,
    ScalaStringIndexOutOfBoundsException -> JavaStringIndexOutOfBoundsException,
    ScalaSuccess -> JavaTry,
    ScalaThrowable -> JavaThrowable,
    ScalaTry -> JavaTry,
    ScalaUnit -> t"void",
    ScalaUnsupportedOperationException -> JavaUnsupportedOperationException,
    ScalaVector -> JavaList
  )

  override def transform(scalaType: Type.Select): Option[Type] = TreeKeyedMap.get(ScalaTypeToJavaType, scalaType)
}
