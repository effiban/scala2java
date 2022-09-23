package effiban.scala2java.entities

import scala.meta.{Name, Type}

case class SealedHierarchies(sealedNameToSubTypeNames: Map[Type.Name, List[Name]] = Map.empty) {

  private val allSubTypeNames = sealedNameToSubTypeNames.values
    .flatten
    .toList
    .distinctBy(_.value)
    .toSet

  def getSubTypeNames(typeName: Type.Name): List[Name] =
    sealedNameToSubTypeNames.view
      .filterKeys(_.value == typeName.value)
      .values
      .flatten
      .toList

  def isSubType(name: Name): Boolean = allSubTypeNames.exists(_.value == name.value)

  def asStringMap(): Map[String, List[String]] =
    sealedNameToSubTypeNames.map { case (typeName, names) => (typeName.value, names.map(_.value)) }
}
