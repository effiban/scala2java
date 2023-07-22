package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.traversers.results.TraitTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.Defn.Trait

trait TraitTraverser {
  def traverse(traitDef: Trait, context: ClassOrTraitContext = ClassOrTraitContext()): TraitTraversalResult
}

private[traversers] class TraitTraverserImpl(statModListTraverser: => StatModListTraverser,
                                             typeParamTraverser: => TypeParamTraverser,
                                             templateTraverser: => TemplateTraverser) extends TraitTraverser {

  override def traverse(traitDef: Trait, context: ClassOrTraitContext = ClassOrTraitContext()): TraitTraversalResult = {
    val modListTraversalResult = statModListTraverser.traverse(ModifiersContext(traitDef, JavaTreeType.Interface, context.javaScope))
    val traversedTypeParams = traitDef.tparams.map(typeParamTraverser.traverse)
    val templateContext = TemplateContext(javaScope = JavaScope.Interface)
    val templateTraversalResult = templateTraverser.traverse(traitDef.templ, templateContext)

    TraitTraversalResult(
      scalaMods = modListTraversalResult.scalaMods,
      javaModifiers = modListTraversalResult.javaModifiers,
      name = traitDef.name,
      tparams = traversedTypeParams,
      inits = templateTraversalResult.inits,
      self = templateTraversalResult.self,
      statResults = templateTraversalResult.statResults
    )
  }
}
