package io.github.effiban.scala2java.transformers

import scala.meta.Type

trait TypeNameTransformer {
  def transform(scalaName: Type.Name): String
}

object TypeNameTransformer extends TypeNameTransformer {

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

  override def transform(scalaName: Type.Name): String = {
    ScalaTypeNameToJavaTypeName.getOrElse(scalaName.value, scalaName.value)
  }
}
