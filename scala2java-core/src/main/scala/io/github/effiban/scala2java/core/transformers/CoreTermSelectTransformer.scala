package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.Term

object CoreTermSelectTransformer extends TermSelectTransformer {

  private final val TupleElementRegex = "_(\\d)".r

  // Transform a Scala-specific qualified name into an equivalent in Java
  // Either and Try use the syntax of the VAVR framework (Maven: io.vavr:vavr)
  override def transform(termSelect: Term.Select, context: TermSelectTransformationContext = TermSelectTransformationContext()): Option[Term] = {
    (termSelect.qual, termSelect.name) match {
      case(qual, Term.Name(TupleElementRegex(index))) => Some(Term.Select(qual, Term.Name(s"v$index")))
      case _ => None
    }
  }
}

