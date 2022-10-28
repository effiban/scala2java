package io.github.effiban.scala2java.transformers

import io.github.effiban.scala2java.entities.TermNameValues
import io.github.effiban.scala2java.entities.TermNameValues.{Empty, ScalaNil, ScalaNone, ScalaOption}

import scala.meta.Term

trait TermNameTransformer {
  def transform(termName: Term.Name): Term
}

object TermNameTransformer extends TermNameTransformer {

  private final val TermNameToTerm = Map(
    // Scala 'emptiness' terms will be replace by their corresponding qualified names (argument-less method calls),
    // and then the rest of the traversal will properly convert them into Java
    ScalaNone -> Term.Select(Term.Name(ScalaOption), Term.Name(Empty)),
    ScalaNil -> Term.Select(Term.Name(TermNameValues.List), Term.Name(Empty))
  )

  override def transform(termName: Term.Name): Term = TermNameToTerm.getOrElse(termName.value, termName)
}
