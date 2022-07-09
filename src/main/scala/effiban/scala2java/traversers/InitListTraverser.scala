package effiban.scala2java.traversers

import scala.meta.Init

trait InitListTraverser {
  def traverse(inits: List[Init]): Unit
}

private[traversers] class InitListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                initTraverser: => InitTraverser) extends InitListTraverser {

  override def traverse(inits: List[Init]): Unit = {
    if (inits.nonEmpty) {
      argumentListTraverser.traverse(args = inits, argTraverser = initTraverser)
    }
  }
}
