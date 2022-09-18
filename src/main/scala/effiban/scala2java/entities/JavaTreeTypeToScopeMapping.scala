package effiban.scala2java.entities

import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

object JavaTreeTypeToScopeMapping {

  private final val map: Map[JavaTreeType, JavaScope] = Map(
    JavaTreeType.Package -> JavaScope.Package,
    JavaTreeType.Class -> JavaScope.Class,
    JavaTreeType.Record -> JavaScope.Class,
    JavaTreeType.Enum -> JavaScope.Enum,
    JavaTreeType.Interface -> JavaScope.Interface
  )

  def apply(javaTreeType: JavaTreeType): JavaScope = map.getOrElse(javaTreeType, JavaScope.Unknown)
}
