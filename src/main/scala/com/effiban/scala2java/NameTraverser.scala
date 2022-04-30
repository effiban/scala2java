package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.{Name, Term, Type}

trait NameTraverser extends ScalaTreeTraverser[Name]

private[scala2java] class NameTraverserImpl(nameAnonymousTraverser: => NameAnonymousTraverser,
                                            nameIndeterminateTraverser: => NameIndeterminateTraverser,
                                            termNameTraverser: => TermNameTraverser,
                                            typeNameTraverser: => TypeNameTraverser) extends NameTraverser {

  override def traverse(name: Name): Unit = name match {
    case anonName: Name.Anonymous => nameAnonymousTraverser.traverse(anonName)
    case indeterminateName: Name.Indeterminate => nameIndeterminateTraverser.traverse(indeterminateName)
    case termName: Term.Name => termNameTraverser.traverse(termName)
    case typeName: Type.Name => typeNameTraverser.traverse(typeName)
    case other => emitComment(s"UNSUPPORTED: $other")
  }

}

object NameTraverser extends NameTraverserImpl(
  NameAnonymousTraverser,
  NameIndeterminateTraverser,
  TermNameTraverser,
  TypeNameTraverser
)
