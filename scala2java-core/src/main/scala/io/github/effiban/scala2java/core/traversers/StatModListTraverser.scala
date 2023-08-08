package io.github.effiban.scala2java.core.traversers

import scala.meta.Mod
import scala.meta.Mod.Annot

trait StatModListTraverser {

  def traverse(mods: List[Mod]): List[Mod]
}

class StatModListTraverserImpl(annotTraverser: => AnnotTraverser) extends StatModListTraverser {

  override def traverse(mods: List[Mod]): List[Mod] = {
    val annots = mods.collect { case annot: Annot => annot }
    val nonAnnots = mods.filterNot(annots.contains)
    val traversedAnnots = annots.map(annotTraverser.traverse)

    traversedAnnots ++ nonAnnots
  }
}
