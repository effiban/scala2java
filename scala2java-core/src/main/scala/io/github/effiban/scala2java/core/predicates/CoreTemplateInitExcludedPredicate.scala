package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate

import scala.meta.{Init, Type}

object CoreTemplateInitExcludedPredicate extends TemplateInitExcludedPredicate {

  private val TypesToExclude = Set[Type](
    Type.Name("Product"),
    Type.Name("Serializable")
  )

  override def apply(init: Init): Boolean = TypesToExclude.exists(_.structure == init.tpe.structure)
}
