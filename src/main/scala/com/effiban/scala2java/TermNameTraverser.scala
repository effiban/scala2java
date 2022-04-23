package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term

object TermNameTraverser extends ScalaTreeTraverser[Term.Name] {

  override def traverse(name: Term.Name): Unit = {
    emit(toJavaName(name))
  }

  private def toJavaName(termName: Term.Name) = {
    // TODO - translate built-in Scala method names to Java equivalents
    termName.value
  }
}
