package io.github.effiban.scala2java.core.predicates

import scala.meta.{Init, Type}

trait TemplateInitIncludedPredicate extends (Init => Boolean)

object TemplateInitIncludedPredicate extends TemplateInitIncludedPredicate {

  private val TypesToExclude = Set[Type](
    Type.Name("Product"),
    Type.Name("Serializable"),
    Type.Name("Enumeration")
  )

  override def apply(init: Init): Boolean = !TypesToExclude.exists(_.structure == init.tpe.structure)
}
