package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.Term.{For, ForYield}
import scala.meta.{Decl, Source, Term}

trait SourceDesugarer extends SameTypeDesugarer[Source]

private[syntactic] class SourceDesugarerImpl(termInterpolateDesugarer: TermInterpolateDesugarer,
                                             forDesugarer: ForDesugarer,
                                             forYieldDesugarer: ForYieldDesugarer,
                                             declValToDeclVarDesugarer: DeclValToDeclVarDesugarer) extends SourceDesugarer {

  override def desugar(source: Source): Source = desugarInner(source) match {
    case desugaredSource: Source => desugaredSource
    case desugared => throw new IllegalStateException(s"The inner transformer should return a Source, but it returned: $desugared")
  }

  private def desugarInner(source: Source) = source.transform {
    case termInterpolate: Term.Interpolate => termInterpolateDesugarer.desugar(termInterpolate)
    case `for`: For => forDesugarer.desugar(`for`)
    case forYield: ForYield => forYieldDesugarer.desugar(forYield)
    case declVal: Decl.Val => declValToDeclVarDesugarer.desugar(declVal)
    case other => other
  }
}

object SourceDesugarer extends SourceDesugarerImpl(
  TermInterpolateDesugarer,
  ForDesugarer,
  ForYieldDesugarer,
  DeclValToDeclVarDesugarer
)
