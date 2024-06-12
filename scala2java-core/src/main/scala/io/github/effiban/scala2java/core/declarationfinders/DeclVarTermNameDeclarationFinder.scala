package io.github.effiban.scala2java.core.declarationfinders

import scala.meta.{Decl, Pat, Term}

trait DeclVarTermNameDeclarationFinder {
  def find(declVar: Decl.Var, termName: Term.Name): Option[Pat]
}

object DeclVarTermNameDeclarationFinder extends DeclVarTermNameDeclarationFinder {

  override def find(declVar: Decl.Var, termName: Term.Name): Option[Pat] =
    declVar.pats.collectFirst {
      case patVar: Pat.Var if patVar.name.value == termName.value => patVar
    }
}
