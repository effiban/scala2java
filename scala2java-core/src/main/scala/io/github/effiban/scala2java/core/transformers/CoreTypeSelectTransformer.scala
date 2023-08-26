package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.entities.TypeSelects._
import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer

import scala.meta.{Type, XtensionQuasiquoteType}

object CoreTypeSelectTransformer extends TypeSelectTransformer {

  private final val ScalaTypeToJavaType = Map[Type.Select, Type.Ref](
    ScalaAny -> t"Object",
    t"scala.AnyRef" -> t"Object",
    ScalaBoolean -> t"boolean",
    ScalaByte -> t"byte",
    ScalaChar -> t"char",
    ScalaShort -> t"short",
    ScalaInt -> t"int",
    ScalaLong -> t"long",
    ScalaFloat -> t"float",
    ScalaDouble -> t"double",
    ScalaUnit -> t"void",
    t"scala.collection.immutable.Seq" -> t"java.util.List",
    t"scala.collection.immutable.Vector" -> t"java.util.List",
    ScalaList -> t"java.util.List",
    t"scala.collection.immutable.Set" -> t"java.util.Set",
    t"scala.collection.immutable.Map" -> t"java.util.Map",
    ScalaOption -> t"java.util.Optional",
    t"scala.concurrent.Future" -> t"java.util.concurrent.CompletableFuture"
  )

  override def transform(scalaType: Type.Select): Option[Type.Ref] = TreeKeyedMap.get(ScalaTypeToJavaType, scalaType)
}
