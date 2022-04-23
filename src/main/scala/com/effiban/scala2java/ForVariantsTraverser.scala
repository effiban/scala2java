package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment
import com.effiban.scala2java.TraversalConstants.JavaPlaceholder

import scala.meta.Term.{Apply, Param, Select}
import scala.meta.{Enumerator, Lit, Pat, Term}

object ForVariantsTraverser {

  def traverse(enumerators: List[Enumerator], body: Term): Unit = {
    GenericTreeTraverser.traverse(translateFor(enumerators, body))
  }

  private def translateFor(enumerators: List[Enumerator],
                           body: Term,
                           maybeCurrentParam: Option[Param] = None): Term = {
    enumerators match {
      case Nil =>
        emitComment("ERROR - for comprehension without enumerators")
        Lit.Unit()
      case theEnumerators =>
        val currentEnumerator :: nextEnumerators = theEnumerators

        val (nextParam, currentTerm) = currentEnumerator match {
          case Enumerator.Generator(pat, term) => (pat2Param(pat), term)
          case Enumerator.CaseGenerator(pat, term) => (pat2Param(pat), term)
          case Enumerator.Val(pat, term) => (pat2Param(pat), term)
          //TODO handle guard, for now returning dummy values
          case Enumerator.Guard(cond) => (Param(Nil, Term.Name(""), None, None), Lit.Unit())
        }

        val currentTranslated = maybeCurrentParam match {
          case Some(currentParam) => Term.Function(List(currentParam), currentTerm)
          case None => currentTerm
        }

        nextEnumerators match {
          case Nil =>
            // Next statement is last (yield)
            val nextTranslated = Term.Function(List(nextParam), body)
            Apply(Select(currentTranslated, Term.Name("map")), List(nextTranslated))
          case theNextEnumerators =>
            // Next statement is not last - recursively translate the rest
            val nextTranslated = translateFor(theNextEnumerators, body, Some(nextParam))
            Apply(Select(currentTranslated, Term.Name("flatMap")), List(nextTranslated))
        }
    }
  }

  private def pat2Param(pat: Pat) = {
    // TODO - improve
    val name = pat match {
      case Pat.Wildcard() => JavaPlaceholder
      case _ => pat.toString()
    }
    Param(mods = List.empty, name = Term.Name(name), decltpe = None, default = None)
  }
}
