package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.Regexes.ScalaTupleElementRegex
import io.github.effiban.scala2java.core.entities.TermSelects.{ScalaNil, ScalaNone}
import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.{Term, XtensionQuasiquoteTerm}

object CoreTermSelectTransformer extends TermSelectTransformer {

  private final val ScalaTermSelectToJavaTerm = Map[Term.Select, Term](
    ScalaNil -> q"java.util.List.of()",
    ScalaNone -> q"java.util.Optional.empty()"
  )

  override def transform(termSelect: Term.Select, context: TermSelectTransformationContext = TermSelectTransformationContext()): Option[Term] = {
    TreeKeyedMap.get(ScalaTermSelectToJavaTerm, termSelect)
      .orElse(transformSpecialCase(termSelect, context))
  }

  private def transformSpecialCase(termSelect: Term.Select, context: TermSelectTransformationContext = TermSelectTransformationContext()): Option[Term] = {
    (termSelect.qual, termSelect.name) match {
      // TODO handle a tuple of 2 args, which are transformed into a Map.Entry before we get here, correctly
      case (qual, Term.Name(ScalaTupleElementRegex(index))) => Some(Term.Select(qual, Term.Name(s"v$index")))
      case _ => None
    }
  }
}

