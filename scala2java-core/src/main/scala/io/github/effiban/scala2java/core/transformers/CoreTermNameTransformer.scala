package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermNameValues
import io.github.effiban.scala2java.core.entities.TermNameValues.{Empty, ScalaNil, ScalaNone, ScalaOption}
import io.github.effiban.scala2java.spi.transformers.TermNameTransformer

import scala.meta.Term

object CoreTermNameTransformer extends TermNameTransformer {

  private final val TermNameToTerm = Map(
    // Scala 'emptiness' terms will be replace by their corresponding qualified names (argument-less method calls),
    // and then the rest of the traversal will properly convert them into Java
    ScalaNone -> Term.Select(Term.Name(ScalaOption), Term.Name(Empty)),
    ScalaNil -> Term.Select(Term.Name(TermNameValues.List), Term.Name(Empty))
  )

  override def transform(termName: Term.Name): Option[Term] = TermNameToTerm.get(termName.value)
}
