package com.effiban.scala2java

import scala.meta.Term.ForYield

object ForYieldTraverser extends ScalaTreeTraverser[ForYield] {

  def traverse(forYield: ForYield): Unit = {
    ForVariantsTraverser.traverse(forYield.enums, forYield.body)
  }
}
