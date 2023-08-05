package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.{Ctor, Defn, Init, Mod, Name, Self, Template, Type}

@deprecated
case class CaseClassTraversalResult(scalaMods: List[Mod] = Nil,
                                    javaModifiers: List[JavaModifier] = Nil,
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

  val templateResult: TemplateTraversalResult = TemplateTraversalResult(
    maybeInheritanceKeyword = maybeInheritanceKeyword,
    inits = inits,
    self = self,
    statResults = statResults
  )
}
