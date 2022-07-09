package effiban.scala2java.traversers

import scala.meta.Pat

trait PatListTraverser {
  def traverse(pats: List[Pat]): Unit
}

private[traversers] class PatListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                               patTraverser: => PatTraverser) extends PatListTraverser {

  override def traverse(pats: List[Pat]): Unit = {
    if (pats.nonEmpty) {
      argumentListTraverser.traverse(args = pats,
        argTraverser = patTraverser,
        onSameLine = true)
    }
  }
}

object PatListTraverser extends PatListTraverserImpl(ArgumentListTraverser, PatTraverser)
