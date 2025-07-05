package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer

import scala.meta.{Type, XtensionQuasiquoteType}

object CoreTypeSelectTransformer extends TypeSelectTransformer {

  // Please keep in alphabetical order
  private final val ScalaTypeToJavaType = Map[Type.Select, Type](
    ScalaAny -> JavaObject,
    ScalaAnyVal -> JavaObject,
    ScalaBigDecimal -> JavaBigDecimal,
    ScalaBigInt -> JavaBigInt,
    ScalaBoolean -> t"boolean",
    ScalaByte -> t"byte",
    ScalaChar -> t"char",
    ScalaDouble -> t"double",
    ScalaEither -> JavaEither,
    ScalaFailure -> JavaTry,
    ScalaFloat -> t"float",
    ScalaFunction0 -> JavaSupplier,
    ScalaFunction1 -> JavaFunction,
    ScalaFunction2 -> JavaBiFunction,
    ScalaFunction3 -> JavaFunction3,
    ScalaFunction4 -> JavaFunction4,
    ScalaFunction5 -> JavaFunction5,
    ScalaFunction6 -> JavaFunction6,
    ScalaFunction7 -> JavaFunction7,
    ScalaFunction8 -> JavaFunction8,
    ScalaFunction9 -> JavaFunction9,
    ScalaFunction10 -> JavaFunction10,
    ScalaFunction11 -> JavaFunction11,
    ScalaFunction12 -> JavaFunction12,
    ScalaFunction13 -> JavaFunction13,
    ScalaFunction14 -> JavaFunction14,
    ScalaFunction15 -> JavaFunction15,
    ScalaFunction16 -> JavaFunction16,
    ScalaFuture -> JavaCompletableFuture,
    ScalaIndexedSeq -> JavaList,
    ScalaInt -> t"int",
    ScalaIterable -> JavaIterable,
    ScalaIterator -> JavaIterator,
    ScalaLazyList -> JavaStream,
    ScalaLeft -> JavaEither,
    ScalaLinearSeq -> JavaList,
    ScalaList -> JavaList,
    ScalaLong -> t"long",
    ScalaMap -> JavaMap,
    ScalaNothing -> JavaObject,
    ScalaOption -> JavaOptional,
    ScalaRange -> t"java.util.List[java.lang.Integer]",
    ScalaRight -> JavaEither,
    ScalaSeq -> JavaList,
    ScalaSet -> JavaSet,
    ScalaShort -> t"short",
    ScalaSome -> JavaOptional,
    ScalaStream -> JavaStream,
    ScalaStringBuilder -> JavaStringBuilder,
    ScalaSuccess -> JavaTry,
    ScalaTry -> JavaTry,
    ScalaUnit -> t"void",
    ScalaVector -> JavaList
  )

  override def transform(scalaType: Type.Select): Option[Type] = TreeKeyedMap.get(ScalaTypeToJavaType, scalaType)
}
