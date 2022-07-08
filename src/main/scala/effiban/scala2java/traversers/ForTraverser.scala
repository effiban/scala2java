package effiban.scala2java.traversers

import scala.meta.Term
import scala.meta.Term.For

trait ForTraverser extends ScalaTreeTraverser[For]

private[scala2java] class ForTraverserImpl(forVariantsTraverser: => ForVariantTraverser) extends ForTraverser {

  override def traverse(`for`: For): Unit = {
    forVariantsTraverser.traverse(`for`.enums, `for`.body, Term.Name("forEach"))
  }
}

object ForTraverser extends ForTraverserImpl(ForVariantTraverser)
