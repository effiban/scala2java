package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaAny, ScalaUnit}
import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer

import scala.meta.{Type, XtensionQuasiquoteType}

object CoreTypeSelectTransformer extends TypeSelectTransformer {

  private final val ScalaTypeToJavaType = Map[Type.Select, Type.Ref](
    ScalaAny -> t"Object",
    t"scala.AnyRef" -> t"Object",
    t"scala.Boolean" -> t"boolean",
    t"scala.Byte" -> t"byte",
    t"scala.Char" -> t"char",
    t"scala.Short" -> t"short",
    t"scala.Int" -> t"int",
    t"scala.Long" -> t"long",
    t"scala.Float" -> t"float",
    t"scala.Double" -> t"double",
    ScalaUnit -> t"void",
    t"scala.collection.immutable.Seq" -> t"java.util.List",
    t"scala.collection.immutable.Vector" -> t"java.util.List",
    t"scala.collection.immutable.List" -> t"java.util.List",
    t"scala.collection.immutable.Set" -> t"java.util.Set",
    t"scala.collection.immutable.Map" -> t"java.util.Map",
    t"scala.Option" -> t"java.util.Optional",
    t"scala.concurrent.Future" -> t"java.util.concurrent.CompletableFuture"
  )

  override def transform(scalaType: Type.Select): Option[Type.Ref] = TreeKeyedMap.get(ScalaTypeToJavaType, scalaType)
}
