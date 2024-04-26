package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer
import io.github.effiban.scala2java.core.entities.TermNameValues.Apply
import io.github.effiban.scala2java.spi.predicates.TermSelectHasApplyMethod

import scala.meta.Term

trait TermApplyFunDesugarer extends SameTypeDesugarer[Term.Apply]

private[semantic] class TermApplyFunDesugarerImpl(termSelectHasApplyMethod: TermSelectHasApplyMethod,
                                                  evaluatedTermSelectQualDesugarer: => EvaluatedTermSelectQualDesugarer,
                                                  termApplyTypeFunDesugarer: => TermApplyTypeFunDesugarer,
                                                  evaluatedTermDesugarer: => EvaluatedTermDesugarer)
  extends TermApplyFunDesugarer {

  override def desugar(termApply: Term.Apply): Term.Apply = {
    import termApply._

    val desugaredFun = fun match {

      case name: Term.Name => name

      case select: Term.Select if termSelectHasApplyMethod(select) => toQualifiedApply(select)
      case select: Term.Select => desugarSelectQual(select)

      case applyType@Term.ApplyType(_: Term.Name, _) => applyType

      case Term.ApplyType(select: Term.Select, types) if termSelectHasApplyMethod(select) => Term.ApplyType(toQualifiedApply(select), types)

      case applyType: Term.ApplyType => desugarApplyTypeFun(applyType)

      // Invocation of lambda - we must add the 'apply' explicitly just as for case classes and objects
      case termFunction: Term.Function => desugarSelectQual(toQualifiedApply(termFunction))

      case aFun => evaluatedTermDesugarer.desugar(aFun)
    }
    termApply.copy(fun = desugaredFun)
  }

  private def desugarSelectQual(termSelect: Term.Select) = evaluatedTermSelectQualDesugarer.desugar(termSelect)

  private def desugarApplyTypeFun(termApplyType: Term.ApplyType) = termApplyTypeFunDesugarer.desugar(termApplyType)

  private def toQualifiedApply(term: Term) = Term.Select(term, Term.Name(Apply))

}