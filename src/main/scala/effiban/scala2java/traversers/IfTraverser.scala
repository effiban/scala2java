package effiban.scala2java.traversers

import effiban.scala2java.entities.Decision.{Decision, No}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Lit
import scala.meta.Term.If

trait IfTraverser {
  def traverse(`if`: If, shouldReturnValue: Decision = No): Unit
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
    blockTraverser.traverse(`if`.thenp, shouldReturnValue)
    `if`.elsep match {
      case Lit.Unit() =>
      case elsep =>
        write("else")
        blockTraverser.traverse(elsep, shouldReturnValue)
    }
  }
}
