package com.effiban.scala2java

import scala.meta.Term.For

trait ForTraverser extends ScalaTreeTraverser[For]

object ForTraverser extends ForTraverser {

  override def traverse(`for`: For): Unit = {
    ForVariantsTraverser.traverse(`for`.enums, `for`.body)
  }
}
