package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.writers.JavaWriter

import scala.meta.Init

trait InitTraverser extends ScalaTreeTraverser[Init]

private[traversers] class InitTraverserImpl(typeTraverser: => TypeTraverser,
                                            termListTraverser: => TermListTraverser)
                                           (implicit javaWriter: JavaWriter) extends InitTraverser {

  // An 'Init' is a parent of a type in its declaration
  override def traverse(init: Init): Unit = {
    typeTraverser.traverse(init.tpe)

    // TODO handle name (not sure what this is for, it seems to be Name.Anonymous always)

    val args = init.argss.flatten
    if (args.nonEmpty) {
      termListTraverser.traverse(init.argss.flatten, maybeEnclosingDelimiter = Some(Parentheses))
    }
  }
}

object InitTraverser extends InitTraverserImpl(TypeTraverser, TermListTraverser)
