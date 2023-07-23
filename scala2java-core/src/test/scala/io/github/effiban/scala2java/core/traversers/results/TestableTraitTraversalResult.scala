package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Defn.Trait

object TestableTraitTraversalResult {

  def apply(defnTrait: Trait,
            javaModifiers: List[JavaModifier] = Nil,
            statResults: List[PopulatedStatTraversalResult] = Nil): TraitTraversalResult =
    TraitTraversalResult(
      scalaMods = defnTrait.mods,
      javaModifiers = javaModifiers,
      name = defnTrait.name,
      tparams = defnTrait.tparams,
      inits = defnTrait.templ.inits,
      self = defnTrait.templ.self,
      statResults = statResults
    )
}
