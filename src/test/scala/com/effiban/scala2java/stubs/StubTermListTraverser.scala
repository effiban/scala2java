package com.effiban.scala2java.stubs

import com.effiban.scala2java.{DualDelimiterType, JavaEmitter, TermListTraverser}

import scala.meta.Term

class StubTermListTraverser(implicit javaEmitter: JavaEmitter) extends TermListTraverser {

  import javaEmitter._

  override def traverse(terms: List[Term],
                        onSameLine: Boolean = false,
                        maybeWrappingDelimiterType: Option[DualDelimiterType] = None): Unit = {
    maybeWrappingDelimiterType.foreach(emitArgumentsStart)
    terms.zipWithIndex.foreach { case (term, idx) =>
      emit(term.toString())
      if (idx < terms.size - 1) {
        emitListSeparator()
        if (onSameLine) emit(" ") else emitLine()
      }
    }
    maybeWrappingDelimiterType.foreach(emitArgumentsEnd)
  }
}
