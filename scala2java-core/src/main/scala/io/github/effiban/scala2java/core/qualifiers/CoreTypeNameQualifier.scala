package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.entities.TypeSelects._

import scala.meta.{Type, XtensionQuasiquoteType}

trait CoreTypeNameQualifier {
  def qualify(typeName: Type.Name): Option[Type.Select]
}

object CoreTypeNameQualifier extends CoreTypeNameQualifier {

  // TODO consider using reflection or semantic information instead of this hard-coded and incomplete mapping
  private final val QualifiedTypeMapping = Map[Type.Name, Type.Select](
    t"Any" -> ScalaAny,
    t"AnyRef" -> t"scala.AnyRef",
    t"Boolean" -> t"scala.Boolean",
    t"Byte" -> t"scala.Byte",
    t"Char" -> t"scala.Char",
    t"Short" -> t"scala.Short",
    t"Int" -> ScalaInt,
    t"Long" -> t"scala.Long",
    t"Float" -> t"scala.Float",
    t"Double" -> ScalaDouble,
    t"Unit" -> ScalaUnit,
    t"Seq" -> t"scala.collection.immutable.Seq",
    t"Vector" -> t"scala.collection.immutable.Vector",
    t"List" -> ScalaList,
    t"Set" -> t"scala.collection.immutable.Set",
    t"Map" -> t"scala.collection.immutable.Map",
    t"Option" -> ScalaOption,
    t"Future" -> t"scala.concurrent.Future"
  )

  override def qualify(typeName: Type.Name): Option[Type.Select] = TreeKeyedMap.get(QualifiedTypeMapping, typeName)
}
