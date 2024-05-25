package io.github.effiban.scala2java.core.desugarers.syntactic

import scala.meta.Term

object TermApplyInfixToTupleDesugarer extends TermApplyInfixDesugarer {

  override def desugar(termApplyInfix: Term.ApplyInfix): Term = {
    // Here assuming that the infix has been validated beforehand (for correct op and num of args). Not failing if we don't have to
    Term.Tuple(List(termApplyInfix.lhs) ++ termApplyInfix.args)
  }
}
