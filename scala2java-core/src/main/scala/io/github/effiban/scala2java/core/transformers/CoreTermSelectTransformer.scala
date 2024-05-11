package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermSelects.{ScalaNil, ScalaNone}
import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.{Term, XtensionQuasiquoteTerm}

object CoreTermSelectTransformer extends TermSelectTransformer {

  private final val ScalaTermSelectToJavaTerm = Map[Term.Select, Term](
    ScalaNil -> q"java.util.List.of()",
    ScalaNone -> q"java.util.Optional.empty()"
  )

  override def transform(termSelect: Term.Select): Option[Term] = {
    TreeKeyedMap.get(ScalaTermSelectToJavaTerm, termSelect)
  }
}

