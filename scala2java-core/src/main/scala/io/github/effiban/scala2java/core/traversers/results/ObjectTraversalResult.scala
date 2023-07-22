package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.{Defn, Init, Mod, Name, Self, Template, Term}

case class ObjectTraversalResult(scalaMods: List[Mod] = Nil,
                                 javaModifiers: List[JavaModifier] = Nil,
                                 javaTypeKeyword: JavaKeyword = JavaKeyword.Class,
                                 name: Term.Name,
                                 maybeInheritanceKeyword: Option[JavaKeyword] = None,
                                 inits: List[Init] = Nil,
                                 self: Self = Self(Name.Anonymous(), None),
                                 statResults: List[PopulatedStatTraversalResult] = Nil) extends DefnTraversalResult {

  override val tree: Defn.Object = Defn.Object(
    mods = scalaMods,
    name = name,
    templ = Template(
      early = Nil,
      inits = inits,
      self = self,
      stats = statResults.map(_.tree)
    )
  )
}
