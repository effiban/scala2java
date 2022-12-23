package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.TypeNameTransformer

import scala.meta.Type

object CoreTypeNameTransformer extends TypeNameTransformer {

  private final val ScalaTypeNameToJavaTypeName = Map(
    "Any" -> "Object",
    "AnyRef" -> "Object",
    "Boolean" -> "boolean",
    "Byte" -> "byte",
    "Short" -> "short",
    "Int" -> "int",
    "Long" -> "long",
    "Float" -> "float",
    "Double" -> "double",
    "Unit" -> "void",
    "Array" -> "Object[]",
    "Seq" -> "List",
    "Vector" -> "List",
    "Option" -> "Optional",
    "Future" -> "CompletableFuture"
  )

  override def transform(scalaName: Type.Name): Type.Name = {
    Type.Name(ScalaTypeNameToJavaTypeName.getOrElse(scalaName.value, scalaName.value))
  }
}
