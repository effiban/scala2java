package effiban.scala2java.classifiers

import effiban.scala2java.entities.ScalaOperatorName
import effiban.scala2java.entities.ScalaOperatorName.{To, Until}

import scala.meta.Term

trait TermApplyInfixClassifier {

  def isRange(termApplyInfix: Term.ApplyInfix): Boolean

  def isAssociation(termApplyInfix: Term.ApplyInfix): Boolean

}
object TermApplyInfixClassifier extends TermApplyInfixClassifier {

  private final val RangeOperators = List(To, Until)

  override def isRange(termApplyInfix: Term.ApplyInfix): Boolean = RangeOperators.contains(termApplyInfix.op.value)

  override def isAssociation(termApplyInfix: Term.ApplyInfix): Boolean = termApplyInfix.op.value == ScalaOperatorName.Associate
}
