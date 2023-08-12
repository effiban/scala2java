package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TreeKeyedMap

import scala.meta.{Type, XtensionQuasiquoteType}

trait CoreTypeNameQualifier {
  def qualify(typeName: Type.Name): Option[Type.Select]
}

object CoreTypeNameQualifier extends CoreTypeNameQualifier {

  private final val QualifiedTypeMapping = Map[Type.Name, Type.Select](
    t"Any" -> t"scala.Any",
    t"AnyRef" -> t"scala.AnyRef",
    t"Boolean" -> t"scala.Boolean",
    t"Byte" -> t"scala.Byte",
    t"Char" -> t"scala.Char",
    t"Short" -> t"scala.Short",
    t"Int" -> t"scala.Int",
    t"Long" -> t"scala.Long",
    t"Float" -> t"scala.Float",
    t"Double" -> t"scala.Double",
    t"Unit" -> t"scala.Unit",
    t"Seq" -> t"scala.collection.immutable.Seq",
    t"Vector" -> t"scala.collection.immutable.Vector",
    t"List" -> t"scala.collection.immutable.List",
    t"Set" -> t"scala.collection.immutable.Set",
    t"Map" -> t"scala.collection.immutable.Map",
    t"Option" -> t"scala.Option",
    t"Future" -> t"scala.concurrent.Future"
  )

  override def qualify(typeName: Type.Name): Option[Type.Select] = TreeKeyedMap.get(QualifiedTypeMapping, typeName)
}
