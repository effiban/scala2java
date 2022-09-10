package effiban.scala2java.entities

import effiban.scala2java.entities.JavaTreeType.JavaTreeType

object JavaTreeTypeToScopeMapping {

  private final val map: Map[JavaTreeType, JavaTreeType] = Map(
    JavaTreeType.Package -> JavaTreeType.Package,
    JavaTreeType.Class -> JavaTreeType.Class,
    JavaTreeType.Record -> JavaTreeType.Class,
    JavaTreeType.Interface -> JavaTreeType.Interface
  )

  def apply(javaTreeType: JavaTreeType): JavaTreeType = map.getOrElse(javaTreeType, JavaTreeType.Unknown)
}
