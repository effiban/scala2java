package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.{Source, Term}

trait SourceDesugarer extends SameTypeDesugarer[Source]

private[syntactic] class SourceDesugarerImpl(termInterpolateDesugarer: TermInterpolateDesugarer) extends SourceDesugarer {

  override def desugar(source: Source): Source = desugarInner(source) match {
    case desugaredSource: Source => desugaredSource
    case desugared => throw new IllegalStateException(s"The inner transformer should return a Source, but it returned: $desugared")
  }

  private def desugarInner(source: Source) = source.transform {
    case termInterpolate: Term.Interpolate => termInterpolateDesugarer.desugar(termInterpolate)
    case other => other
  }
}

object SourceDesugarer extends SourceDesugarerImpl(TermInterpolateDesugarer)
