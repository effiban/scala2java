package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.ListTraversalOptions

import scala.meta.Init

trait InitTraverser {
  def traverse(init: Init, ignoreArgs: Boolean = false): Unit
}

private[traversers] class InitTraverserImpl(typeTraverser: => TypeTraverser,
                                            termListTraverser: => TermListTraverser) extends InitTraverser {

  // An 'Init' is an instantiated type, such as with `new` or as a parent in a type definition
  override def traverse(init: Init, ignoreArgs: Boolean = false): Unit = {
    typeTraverser.traverse(init.tpe)

    if (!ignoreArgs) {
      val options = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses))
      termListTraverser.traverse(init.argss.flatten, options)
    }
  }
}
