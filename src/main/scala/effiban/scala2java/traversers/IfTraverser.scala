package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.{Block, If}
import scala.meta.{Lit, Term}

trait IfTraverser {
  def traverse(`if`: If, shouldReturnValue: Boolean = false): Unit
}

private[traversers] class IfTraverserImpl(termTraverser: => TermTraverser,
                                          blockTraverser: => BlockTraverser)
                                         (implicit javaWriter: JavaWriter) extends IfTraverser {

  import javaWriter._

  override def traverse(`if`: If, shouldReturnValue: Boolean = false): Unit = {
    //TODO handle mods (what do they represent in an 'if'?...)
    write("if (")
    termTraverser.traverse(`if`.cond)
    write(")")
    traverseClause(`if`.thenp, shouldReturnValue)
    `if`.elsep match {
      case Lit.Unit() =>
      case elsep =>
        write("else")
        traverseClause(elsep, shouldReturnValue)
    }
  }

  private def traverseClause(clause: Term, shouldReturnValue: Boolean): Unit = {
    clause match {
      case block: Block => blockTraverser.traverse(block = block, shouldReturnValue = shouldReturnValue)
      case term => blockTraverser.traverse(block = Block(List(term)), shouldReturnValue = shouldReturnValue)
    }
  }
}
