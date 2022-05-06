package com.effiban.scala2java

import scala.meta.Type

trait TypeNameTraverser extends ScalaTreeTraverser[Type.Name]

private[scala2java] class TypeNameTraverserImpl(javaEmitter: JavaEmitter) extends TypeNameTraverser {

  import javaEmitter._

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

  override def traverse(name: Type.Name): Unit = {
    emit(toJavaName(name))
  }

  private def toJavaName(typeName: Type.Name) = {
    ScalaTypeNameToJavaTypeName.getOrElse(typeName.value, typeName.value)
  }
}

object TypeNameTraverser extends TypeNameTraverserImpl(JavaEmitter)