package effiban.scala2java.classifiers

import scala.meta.Type

trait TypeNameClassifier {

  def isParameterizedType(typeName: Type.Name): Boolean
}

object TypeNameClassifier extends TypeNameClassifier {

  private val ParameterizedTypeNames = Set(
    "Option",
    "Either",
    "Future",
    "Stream",
    "Array",
    "List",
    "Vector",
    "Seq",
    "Set",
    "Map"
  )

  override def isParameterizedType(typeName: Type.Name): Boolean = ParameterizedTypeNames.contains(typeName.value)
}
