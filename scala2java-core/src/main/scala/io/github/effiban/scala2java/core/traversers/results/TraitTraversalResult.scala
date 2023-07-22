package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Defn.Trait
import scala.meta.{Ctor, Init, Mod, Name, Self, Template, Type}

case class TraitTraversalResult(scalaMods: List[Mod] = Nil,
                                javaModifiers: List[JavaModifier] = Nil,
                                name: Type.Name,
                                tparams: List[Type.Param] = Nil,
                                inits: List[Init] = Nil,
                                self: Self = Self(Name.Anonymous(), None),
                                statResults: List[PopulatedStatTraversalResult] = Nil) {
  val `trait`: Trait = Trait(
    mods = scalaMods,
    name = name,
    tparams = tparams,
    ctor = Ctor.Primary(mods = Nil, name = Name.Anonymous(), paramss = List(Nil)),
    templ = Template(
      early = Nil,
      inits = inits,
      self = self,
      stats = statResults.map(_.tree)
    )
  )
}
