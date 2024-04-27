package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.{TreeKeyedMap, TypeNames}
import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer

import scala.meta.{Type, XtensionQuasiquoteType}

object CoreTypeSelectTransformer extends TypeSelectTransformer {

  // Please keep in alphabetical order
  private final val ScalaTypeToJavaType = Map[Type.Select, Type](
    ScalaAbstractMethodError -> t"AbstractMethodError",
    ScalaAny -> t"Object",
    ScalaAnyRef -> t"Object",
    ScalaAnyVal -> t"Object",
    ScalaArrayIndexOutOfBoundsException -> t"ArrayIndexOutOfBoundsException",
    ScalaBigDecimal -> JavaBigDecimal,
    ScalaBigInt -> JavaBigInt,
    ScalaBoolean -> t"boolean",
    ScalaByte -> t"byte",
    ScalaChar -> t"char",
    ScalaClass -> TypeNames.Class,
    ScalaClassCastException -> t"ClassCastException",
    ScalaCloneable -> t"Cloneable",
    ScalaDouble -> t"double",
    ScalaEither -> JavaEither,
    ScalaError -> t"Error",
    ScalaException -> t"Exception",
    ScalaFailure -> JavaTry,
    ScalaFloat -> t"float",
    ScalaFunction -> JavaFunction,
    ScalaFuture -> JavaCompletableFuture,
    ScalaIllegalArgumentException -> t"IllegalArgumentException",
    ScalaIndexedSeq -> JavaList,
    ScalaIndexOutOfBoundsException -> t"IndexOutOfBoundsException",
    ScalaInt -> t"int",
    ScalaInterruptedException -> t"InterruptedException",
    ScalaIterable -> t"Iterable",
    ScalaIterator -> t"Iterator",
    ScalaLazyList -> JavaStream,
    ScalaLeft -> JavaEither,
    ScalaList -> JavaList,
    ScalaLong -> t"long",
    ScalaMap -> JavaMap,
    ScalaNoSuchElementException -> JavaNoSuchElementException,
    ScalaNullPointerException -> t"NullPointerException",
    ScalaNumberFormatException -> t"NumberFormatException",
    ScalaOption -> JavaOptional,
    ScalaRange -> t"java.util.List[Integer]",
    ScalaRight -> JavaEither,
    ScalaRuntimeException -> t"RuntimeException",
    ScalaSeq -> JavaList,
    ScalaSet -> JavaSet,
    ScalaSerializable -> JavaSerializable,
    ScalaShort -> t"short",
    ScalaSome -> JavaOptional,
    ScalaStream -> JavaStream,
    ScalaString -> t"String",
    ScalaStringBuilder -> t"StringBuilder",
    ScalaStringIndexOutOfBoundsException -> t"StringIndexOutOfBoundsException",
    ScalaSuccess -> JavaTry,
    ScalaThrowable -> t"Throwable",
    ScalaTry -> JavaTry,
    ScalaUnit -> t"void",
    ScalaUnsupportedOperationException -> t"UnsupportedOperationException",
    ScalaVector -> JavaList
  )

  override def transform(scalaType: Type.Select): Option[Type] = TreeKeyedMap.get(ScalaTypeToJavaType, scalaType)
}
