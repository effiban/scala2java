package io.github.effiban.scala2java.core.qualifiers

import scala.meta.{Term, Type}

trait TermApplyInfixQualifier {

  def qualify(termApplyInfix: Term.ApplyInfix, context: QualificationContext = QualificationContext()): Term.ApplyInfix
}

private[qualifiers] class TermApplyInfixQualifierImpl(treeQualifier: => TreeQualifier) extends TermApplyInfixQualifier {

  override def qualify(termApplyInfix: Term.ApplyInfix, context: QualificationContext = QualificationContext()): Term.ApplyInfix = {
    import termApplyInfix._

    val qualifiedLhs = treeQualifier.qualify(lhs, context).asInstanceOf[Term]
    val qualifedTargs = targs.map(treeQualifier.qualify(_, context).asInstanceOf[Type])
    val qualifedArgs = args.map(treeQualifier.qualify(_, context).asInstanceOf[Term])
    termApplyInfix.copy(lhs = qualifiedLhs, targs = qualifedTargs, args = qualifedArgs)
  }
}
