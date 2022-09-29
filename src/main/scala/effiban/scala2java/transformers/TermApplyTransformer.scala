package effiban.scala2java.transformers

import scala.annotation.tailrec
import scala.meta.Term

trait TermApplyTransformer {
  def transform(termApply: Term.Apply): Term.Apply
}

class TermApplyTransformerImpl(termApplyNameTransformer: TermApplyNameTransformer) extends TermApplyTransformer {

  // Transform any method invocations which have a Scala-specific naming or style into Java equivalents
  @tailrec
  override final def transform(termApply: Term.Apply): Term.Apply = {
    termApply match {
      case Term.Apply(future@Term.Name("Future"), List(arg)) => Term.Apply(transformName(future), List(Term.Function(Nil, arg)))
      case Term.Apply(Term.ApplyType(future@Term.Name("Future"), types), List(arg)) =>
        Term.Apply(Term.ApplyType(transformName(future), types), List(Term.Function(Nil, arg)))

      case Term.Apply(name : Term.Name, args) => Term.Apply(transformName(name), args)
      case Term.Apply(Term.ApplyType(name: Term.Name, types), args) => Term.Apply(Term.ApplyType(transformName(name), types), args)

      // Invocation of method with more than one param list
      case Term.Apply(Term.Apply(fun, args1), args2) => transform(Term.Apply(fun, args1 ++ args2))

      case other => other
    }
  }

  private def transformName(name: Term.Name): Term = termApplyNameTransformer.transform(name)

}

object TermApplyTransformer extends TermApplyTransformerImpl(TermApplyNameTransformer)
