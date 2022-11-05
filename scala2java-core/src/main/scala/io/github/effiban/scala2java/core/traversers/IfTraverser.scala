package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.entities.Decision.{Decision, No}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Lit
import scala.meta.Term.If

trait IfTraverser {
  def traverse(`if`: If, shouldReturnValue: Decision = No): Unit

  def traverseAsTertiaryOp(`if`: If): Unit
}

private[traversers] class IfTraverserImpl(termTraverser: => TermTraverser,
                                          blockTraverser: => BlockTraverser)
                                         (implicit javaWriter: JavaWriter) extends IfTraverser {

  import javaWriter._

  override def traverse(`if`: If, shouldReturnValue: Decision = No): Unit = {
    //TODO handle mods (what do they represent in an 'if'?...)
    write("if (")
    termTraverser.traverse(`if`.cond)
    write(")")
    blockTraverser.traverse(`if`.thenp, BlockContext(shouldReturnValue = shouldReturnValue))
    `if`.elsep match {
      case Lit.Unit() =>
      case elsep =>
        //TODO 1. If the 'then' clause returns a value, traverse the 'else' statement only (no 'else' word and no block)
        //TODO 2. If the 'else' clause is itself an 'if', don't wrap it in a block
        write("else")
        blockTraverser.traverse(elsep, BlockContext(shouldReturnValue = shouldReturnValue))
    }
  }

  override def traverseAsTertiaryOp(`if`: If): Unit = {
    write("(")
    termTraverser.traverse(`if`.cond)
    write(") ? ")
    termTraverser.traverse(`if`.thenp)
    write(" : ")
    `if`.elsep match {
      case Lit.Unit() => throw new IllegalStateException("Trying to traverse as a tertiary op with no 'else' clause")
      case elsep => termTraverser.traverse(elsep)
    }
  }
}
