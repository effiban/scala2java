package com.effiban.scala2java

import scala.meta.Term.For

object ForTraverser extends ScalaTreeTraverser[For] {

  def traverse(`for`: For): Unit = {
    ForVariantsTraverser.traverse(`for`.enums, `for`.body)
  }
}
