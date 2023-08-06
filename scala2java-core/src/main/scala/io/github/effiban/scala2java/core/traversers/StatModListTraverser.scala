package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult

import scala.meta.Mod
import scala.meta.Mod.Annot

trait StatModListTraverser {

  @deprecated
  def traverse(modifiersContext: ModifiersContext): ModListTraversalResult

  def traverse(mods: List[Mod]): List[Mod]
}

class StatModListTraverserImpl(annotTraverser: => AnnotTraverser,
                               javaModifiersResolver: JavaModifiersResolver) extends StatModListTraverser {

  override def traverse(modifiersContext: ModifiersContext): ModListTraversalResult = {
    import modifiersContext._
    val annots = scalaMods.collect { case annot: Annot => annot}
    val nonAnnots = scalaMods.filterNot(annots.contains)
    val traversedAnnots = annots.map(annotTraverser.traverse)
    val javaModifiers = javaModifiersResolver.resolve(modifiersContext)

    ModListTraversalResult(traversedAnnots ++ nonAnnots, javaModifiers)
  }

  override def traverse(mods: List[Mod]): List[Mod] = {
    val annots = mods.collect { case annot: Annot => annot }
    val nonAnnots = mods.filterNot(annots.contains)
    val traversedAnnots = annots.map(annotTraverser.traverse)

    traversedAnnots ++ nonAnnots
  }
}
