package io.github.effiban.scala2java.core.declarationfinders

import scala.meta.Term

trait TermParamTermNameDeclarationFinder {
  def find(termParam: Term.Param, termName: Term.Name): Option[Term.Param]
}

object TermParamTermNameDeclarationFinder extends TermParamTermNameDeclarationFinder {

  override def find(termParam: Term.Param, termName: Term.Name): Option[Term.Param] =
    if (termParam.name.value == termName.value) Some(termParam) else None
}
