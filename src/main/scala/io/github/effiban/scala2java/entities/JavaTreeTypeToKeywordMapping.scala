package io.github.effiban.scala2java.entities

import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType

object JavaTreeTypeToKeywordMapping {

  private final val map: Map[JavaTreeType, JavaKeyword] = Map(
    JavaTreeType.Package -> JavaKeyword.Package,
    JavaTreeType.Class -> JavaKeyword.Class,
    JavaTreeType.Record -> JavaKeyword.Record,
    JavaTreeType.Enum -> JavaKeyword.Enum,
    JavaTreeType.Interface -> JavaKeyword.Interface
  )

  def apply(javaTreeType: JavaTreeType): JavaKeyword = map.getOrElse(javaTreeType, JavaKeyword.NoKeyword)
}
