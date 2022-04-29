package com.effiban.scala2java

import scala.meta.Term.ForYield

trait ForYieldTraverser extends ScalaTreeTraverser[ForYield]

object ForYieldTraverser extends ForYieldTraverser {

  override def traverse(forYield: ForYield): Unit = {
    ForVariantsTraverser.traverse(forYield.enums, forYield.body)
  }
}
