package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, TermTraverser}

import scala.meta.{Lit, Term}

class StubTermTraverser(implicit javaEmitter: JavaEmitter) extends TermTraverser {
  import javaEmitter._

  override def traverse(term: Term): Unit =
    term match {
      case name: Term.Name => emit(name.value)
      case litStr: Lit.String => emit(s"\"${litStr.value}\"")
      case lit: Lit => emit(lit.value.toString)
      case other => emit(other.toString())
    }
}
