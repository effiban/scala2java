package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.resolvers.JavaExtraModifierResolver
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Mod.Annot
import scala.meta.{Mod, Term}

trait TermParamModListTraverser {
  def traverse(termParam: Term.Param, javaScope: JavaScope): List[Mod]
}

class TermParamModListTraverserImpl(annotTraverser: => AnnotTraverser,
                                    javaFinalModifierResolver: JavaExtraModifierResolver) extends TermParamModListTraverser {

  override def traverse(termParam: Term.Param, javaScope: JavaScope): List[Mod] = {
    val mods = termParam.mods
    val annots = mods.collect { case annot: Annot => annot}
    val nonAnnots = mods.filterNot(annots.contains)

    val traversedAnnots = annots.map(annotTraverser.traverse)
    val modifiersContext = ModifiersContext(termParam, JavaTreeType.Parameter, javaScope)

    val maybeFinalMod: Option[Mod] = javaFinalModifierResolver.resolve(modifiersContext).map(_ => Mod.Final())
    val traversedNonAnnots = (nonAnnots ++ maybeFinalMod).distinct

    traversedAnnots ++ traversedNonAnnots
  }
}
