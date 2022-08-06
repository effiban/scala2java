package effiban.scala2java.transformers

import scala.meta.Term.{Apply, Param, Select}
import scala.meta.{Enumerator, Lit, Pat, Term}

trait ForVariantToTermApplyTransformer {
  val patToTermParamTransformer: PatToTermParamTransformer

  val intermediateFunctionName: Term.Name
  val finalFunctionName: Term.Name

  def transform(enumerators: List[Enumerator], body: Term): Term.Apply = {
    enumerators match {
      case Nil => throw new IllegalStateException("A 'for' variant must have enumerators")
      case theEnumerators => transformInner(theEnumerators, body)
    }
  }

  private def transformInner(enumerators: List[Enumerator], body: Term): Term.Apply = {
    val currentEnumerator :: nextEnumerators = enumerators

    val (param, adjustedTerm) = currentEnumerator match {
      case Enumerator.Generator(pat, term) => (pat2Param(pat), term)
      //TODO should be converted to partial function
      case Enumerator.CaseGenerator(pat, term) => (pat2Param(pat), term)
      //TODO not sure what this is
      case Enumerator.Val(pat, term) => (pat2Param(pat), term)
      //TODO handle guard, for now returning dummy values
      case Enumerator.Guard(_) => (Param(Nil, Term.Name(""), None, None), Lit.Unit())
    }

    nextEnumerators match {
      case Nil =>
        // This enumerator is the last - invoke the final function
        Apply(Select(adjustedTerm, finalFunctionName), List(Term.Function(List(param), body)))
      case theNextEnumerators =>
        // This enumerator is not the last - invoke the intermediate function and recursively translate the rest
        Apply(
          Select(adjustedTerm, intermediateFunctionName),
          List(Term.Function(List(param), transformInner(theNextEnumerators, body)))
        )
    }
  }

  private def pat2Param(pat: Pat) = {
    // TODO improve default case
    patToTermParamTransformer.transform(pat)
      .getOrElse(Param(mods = List.empty, name = Term.Name(pat.toString()), decltpe = None, default = None))
  }
}
