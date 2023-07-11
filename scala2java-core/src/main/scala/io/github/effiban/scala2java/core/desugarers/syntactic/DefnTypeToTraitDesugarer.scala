package io.github.effiban.scala2java.core.desugarers.syntactic

import scala.meta.Defn.Trait
import scala.meta.{Ctor, Defn, Init, Name, Self, Template}

trait DefnTypeToTraitDesugarer {
  def desugar(defnType: Defn.Type): Trait
}

object DefnTypeToTraitDesugarer extends DefnTypeToTraitDesugarer {

  override def desugar(defnType: Defn.Type): Trait = {

    // The body type is converted into the parent of the resulting Trait
    val init = Init(
      tpe = defnType.body,
      name = Name.Anonymous(),
      argss = List(Nil)
    )
    Trait(
      mods = defnType.mods,
      name = defnType.name,
      tparams = defnType.tparams,
      ctor = Ctor.Primary(mods = Nil, name = Name.Anonymous(), paramss = Nil),
      templ = Template(
        early = Nil,
        inits = List(init),
        self = Self(name = Name.Anonymous(), decltpe = None),
        stats = Nil
      )
    )
  }
}
