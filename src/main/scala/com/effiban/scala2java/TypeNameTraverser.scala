package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

object TypeNameTraverser extends ScalaTreeTraverser[Type.Name] {

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
    "Seq" -> "List",
    "Vector" -> "List",
    "Option" -> "Optional",
    "Nothing" -> "Void"
  )

  def traverse(name: Type.Name): Unit = {
    emit(toJavaName(name))
  }

  private def toJavaName(typeName: Type.Name) = {
    ScalaTypeNameToJavaTypeName.getOrElse(typeName.value, typeName.value)
  }
}
