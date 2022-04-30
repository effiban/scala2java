package com.effiban.scala2java

import scala.meta.Term.ForYield

trait ForYieldTraverser extends ScalaTreeTraverser[ForYield]

private[scala2java] class ForYieldTraverserImpl(forVariantsTraverser: => ForVariantsTraverser) extends ForYieldTraverser {

  override def traverse(forYield: ForYield): Unit = {
    forVariantsTraverser.traverse(forYield.enums, forYield.body)
  }
}

object ForYieldTraverser extends ForYieldTraverserImpl(ForVariantsTraverser)
