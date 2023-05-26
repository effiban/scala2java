package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermNameValues
import io.github.effiban.scala2java.core.entities.TermNameValues.{JavaAbsent, JavaOf, JavaOptional, ScalaNil, ScalaNone}
import io.github.effiban.scala2java.spi.transformers.TermNameTransformer

import scala.meta.Term

object CoreTermNameTransformer extends TermNameTransformer {

  private final val TermNameToTerm = Map(
    ScalaNone -> Term.Apply(Term.Select(Term.Name(JavaOptional), Term.Name(JavaAbsent)), Nil),
    ScalaNil -> Term.Apply(Term.Select(Term.Name(TermNameValues.List), Term.Name(JavaOf)), Nil)
  )

  override def transform(termName: Term.Name): Option[Term] = TermNameToTerm.get(termName.value)
}
