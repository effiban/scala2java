package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.{Name, Term, Type}

object NameTraverser extends ScalaTreeTraverser[Name] {

  override def traverse(name: Name): Unit = name match {
    case anonName: Name.Anonymous => NameAnonymousTraverser.traverse(anonName)
    case indeterminateName: Name.Indeterminate => NameIndeterminateTraverser.traverse(indeterminateName)
    case termName: Term.Name => TermNameTraverser.traverse(termName)
    case typeName: Type.Name => TypeNameTraverser.traverse(typeName)
    case other => emit(s"UNSUPPORTED: $other")
  }

}
