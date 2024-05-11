package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.Regexes.ScalaTupleElementRegex
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermSelectNameTransformer

import scala.meta.Term

object CoreTermSelectNameTransformer extends TermSelectNameTransformer {

  override def transform(termName: Term.Name, context: TermSelectTransformationContext = TermSelectTransformationContext()): Term.Name = {
    termName match {
      // TODO handle a tuple of 2 args, which are transformed into a Map.Entry before we get here, correctly
      case Term.Name(ScalaTupleElementRegex(index)) => Term.Name(s"v$index")
      case aTermName => aTermName
    }
  }
}

