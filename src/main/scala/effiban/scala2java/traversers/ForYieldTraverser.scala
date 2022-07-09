package effiban.scala2java.traversers

import scala.meta.Term
import scala.meta.Term.ForYield

trait ForYieldTraverser extends ScalaTreeTraverser[ForYield]

private[traversers] class ForYieldTraverserImpl(forVariantTraverser: => ForVariantTraverser) extends ForYieldTraverser {

  override def traverse(forYield: ForYield): Unit = {
    forVariantTraverser.traverse(forYield.enums, forYield.body, Term.Name("map"))
  }
}
