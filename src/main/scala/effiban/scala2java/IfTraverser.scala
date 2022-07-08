package effiban.scala2java

import scala.meta.Term.{Block, If}
import scala.meta.{Lit, Term}

trait IfTraverser {
  def traverse(`if`: If, shouldReturnValue: Boolean = false): Unit
}

private[scala2java] class IfTraverserImpl(termTraverser: => TermTraverser,
                                          blockTraverser: => BlockTraverser)
                                         (implicit javaEmitter: JavaEmitter) extends IfTraverser {

  import javaEmitter._

  override def traverse(`if`: If, shouldReturnValue: Boolean = false): Unit = {
    //TODO handle mods (what do they represent in an 'if'?...)
    emit("if (")
    termTraverser.traverse(`if`.cond)
    emit(")")
    traverseClause(`if`.thenp, shouldReturnValue)
    `if`.elsep match {
      case Lit.Unit() =>
      case elsep =>
        emit("else")
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

object IfTraverser extends IfTraverserImpl(TermTraverser, BlockTraverser)
