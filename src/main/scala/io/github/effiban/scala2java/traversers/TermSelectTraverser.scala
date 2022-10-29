package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.TermSelectContext
import io.github.effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.transformers.TermSelectTransformer
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermSelectTraverser {
  def traverse(termSelect: Term.Select, context: TermSelectContext = TermSelectContext()): Unit
}

private[traversers] class TermSelectTraverserImpl(termTraverser: => TermTraverser,
                                                  termNameTraverser: => TermNameTraverser,
                                                  typeListTraverser: => TypeListTraverser,
                                                  termSelectTransformer: TermSelectTransformer)
                                                 (implicit javaWriter: JavaWriter) extends TermSelectTraverser {

  import javaWriter._

  // qualified name
  override def traverse(select: Term.Select, context: TermSelectContext = TermSelectContext()): Unit = {
    val javaSelect = termSelectTransformer.transform(select)
    traverseQualifier(javaSelect.qual)
    writeQualifierSeparator(javaSelect.qual)
    typeListTraverser.traverse(context.appliedTypeArgs)
    termNameTraverser.traverse(javaSelect.name)
  }

  private def traverseQualifier(qualifier: Term): Unit = {
    qualifier match {
      case termFunction: Term.Function =>
        writeArgumentsStart(Parentheses)
        termTraverser.traverse(termFunction)
        writeArgumentsEnd(Parentheses)
      case qual => termTraverser.traverse(qual)
    }
  }

  private def writeQualifierSeparator(qualifier: Term): Unit = {
    qualifier match {
      case _ : Term.Apply => writeLine()
      case _ =>
    }
    javaWriter.writeQualifierSeparator()
  }
}
