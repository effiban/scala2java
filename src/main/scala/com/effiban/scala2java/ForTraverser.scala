package com.effiban.scala2java

import scala.meta.Term.For

trait ForTraverser extends ScalaTreeTraverser[For]

private[scala2java] class ForTraverserImpl(forVariantsTraverser: => ForVariantsTraverser) extends ForTraverser {

  override def traverse(`for`: For): Unit = {
    forVariantsTraverser.traverse(`for`.enums, `for`.body)
  }
}

object ForTraverser extends ForTraverserImpl(ForVariantsTraverser)
