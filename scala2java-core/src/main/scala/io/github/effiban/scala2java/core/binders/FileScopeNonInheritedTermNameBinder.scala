package io.github.effiban.scala2java.core.binders

import io.github.effiban.scala2java.core.declarationfinders.TreeTermNameDeclarationFinder

import scala.meta.{Term, Tree}

trait FileScopeNonInheritedTermNameBinder {
  def bind(termName: Term.Name): Option[Tree]
}

private[binders] class FileScopeNonInheritedTermNameBinderImpl(treeTermNameDeclarationFinder: TreeTermNameDeclarationFinder)
  extends FileScopeNonInheritedTermNameBinder {

  override def bind(termName: Term.Name): Option[Tree] = bindInner(termName, termName)

  private def bindInner(scope: Tree, termName: Term.Name): Option[Tree] = {
    treeTermNameDeclarationFinder.find(scope, termName)
      .orElse(scope.parent.flatMap(scopeParent => bindInner(scopeParent, termName)))
  }
}

object FileScopeNonInheritedTermNameBinder extends FileScopeNonInheritedTermNameBinderImpl(TreeTermNameDeclarationFinder)