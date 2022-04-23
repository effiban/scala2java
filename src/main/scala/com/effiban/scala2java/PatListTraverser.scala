package com.effiban.scala2java

import scala.meta.Pat

object PatListTraverser {

  def traverse(pats: List[Pat]): Unit = {
    if (pats.nonEmpty) {
      ArgumentListTraverser.traverse(args = pats,
        argTraverser = PatTraverser,
        onSameLine = true)
    }
  }
}
