package effiban.scala2java.transformers

import scala.meta.Type

trait ScalaToJavaTypeNameTransformer {
  def transform(scalaName: Type.Name): String
}

object ScalaToJavaTypeNameTransformer extends ScalaToJavaTypeNameTransformer {

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
  )

  override def transform(scalaName: Type.Name): String = {
    ScalaTypeNameToJavaTypeName.getOrElse(scalaName.value, scalaName.value)
  }
}
