package effiban.scala2java.traversers

import scala.meta.Term
import scala.meta.Term.For

trait ForTraverser extends ScalaTreeTraverser[For]

private[traversers] class ForTraverserImpl(forVariantTraverser: => ForVariantTraverser) extends ForTraverser {

  override def traverse(`for`: For): Unit = {
    forVariantTraverser.traverse(`for`.enums, `for`.body, Term.Name("forEach"))
  }
}
