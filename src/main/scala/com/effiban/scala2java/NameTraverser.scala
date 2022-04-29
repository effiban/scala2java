package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.{Name, Term, Type}

trait NameTraverser extends ScalaTreeTraverser[Name]

object NameTraverser extends NameTraverser {

  override def traverse(name: Name): Unit = name match {
    case anonName: Name.Anonymous => NameAnonymousTraverser.traverse(anonName)
    case indeterminateName: Name.Indeterminate => NameIndeterminateTraverser.traverse(indeterminateName)
    case termName: Term.Name => TermNameTraverser.traverse(termName)
    case typeName: Type.Name => TypeNameTraverser.traverse(typeName)
    case other => emitComment(s"UNSUPPORTED: $other")
  }

}
