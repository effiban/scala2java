package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.Defn.Trait
import scala.meta.{Ctor, Name}

trait TraitTraverser {
  def traverse(traitDef: Trait): Trait
}

private[traversers] class TraitTraverserImpl(statModListTraverser: => StatModListTraverser,
                                             typeParamTraverser: => TypeParamTraverser,
                                             templateTraverser: => TemplateTraverser) extends TraitTraverser {

  override def traverse(traitDef: Trait): Trait = {
    val traversedMods = statModListTraverser.traverse(traitDef.mods)
    val traversedTypeParams = traitDef.tparams.map(typeParamTraverser.traverse)
    val templateContext = TemplateContext(javaScope = JavaScope.Interface)
    val traversedTemplate = templateTraverser.traverse(traitDef.templ, templateContext)

    Trait(
      mods = traversedMods,
      name = traitDef.name,
      tparams = traversedTypeParams,
      ctor = Ctor.Primary(mods = Nil, name = Name.Anonymous(), paramss = Nil),
      templ = traversedTemplate
    )
  }
}
