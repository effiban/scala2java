package effiban.scala2java.transformers

import scala.meta.Term

trait TermApplyInfixToMapEntryTransformer {
  def transform(termApplyInfix: Term.ApplyInfix): Term.Apply
}

object TermApplyInfixToMapEntryTransformer extends TermApplyInfixToMapEntryTransformer {

  override def transform(termApplyInfix: Term.ApplyInfix): Term.Apply = {
    // Here assuming that the infix has been validated beforehand (for correct op and num of args). Not failing if we don't have to
    Term.Apply(
      fun = Term.Select(Term.Name("Map"), Term.Name("entry")),
      args = List(termApplyInfix.lhs) ++ termApplyInfix.args
    )
  }
}
