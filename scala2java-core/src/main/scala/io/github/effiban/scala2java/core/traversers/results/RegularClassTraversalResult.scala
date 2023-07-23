package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.{Ctor, Defn, Init, Mod, Name, Self, Template, Type}

case class RegularClassTraversalResult(scalaMods: List[Mod] = Nil,
                                       javaModifiers: List[JavaModifier] = Nil,
                                       javaTypeKeyword: JavaKeyword = JavaKeyword.Class,
                                       name: Type.Name,
                                       tparams: List[Type.Param] = Nil,
                                       ctor: Ctor.Primary,
                                       maybeInheritanceKeyword: Option[JavaKeyword] = None,
                                       inits: List[Init] = Nil,
                                       self: Self = Self(Name.Anonymous(), None),
                                       statResults: List[PopulatedStatTraversalResult] = Nil) extends ClassTraversalResult {

  override val tree: Defn.Class = Defn.Class(
    mods = scalaMods,
    name = name,
    tparams = tparams,
    ctor = ctor,
    templ = Template(
      early = Nil,
      inits = inits,
      self = self,
      stats = statResults.map(_.tree)
    )
  )
}
