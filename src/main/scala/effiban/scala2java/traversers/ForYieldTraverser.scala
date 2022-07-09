package effiban.scala2java.traversers

import scala.meta.Term
import scala.meta.Term.ForYield

trait ForYieldTraverser extends ScalaTreeTraverser[ForYield]

private[traversers] class ForYieldTraverserImpl(forVariantsTraverser: => ForVariantTraverser) extends ForYieldTraverser {

  override def traverse(forYield: ForYield): Unit = {
    forVariantsTraverser.traverse(forYield.enums, forYield.body, Term.Name("map"))
  }
}

object ForYieldTraverser extends ForYieldTraverserImpl(ForVariantTraverser)
