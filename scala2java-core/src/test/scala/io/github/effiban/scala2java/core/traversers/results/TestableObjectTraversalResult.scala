package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.Defn

object TestableObjectTraversalResult {

  def apply(defnObject: Defn.Object,
            javaModifiers: List[JavaModifier] = Nil,
            javaTypeKeyword: JavaKeyword = JavaKeyword.Class,
            maybeInheritanceKeyword: Option[JavaKeyword] = None,
            statResults: List[PopulatedStatTraversalResult] = Nil): ObjectTraversalResult =
    ObjectTraversalResult(
      scalaMods = defnObject.mods,
      javaModifiers = javaModifiers,
      javaTypeKeyword = javaTypeKeyword,
      name = defnObject.name,
      maybeInheritanceKeyword = maybeInheritanceKeyword,
      inits = defnObject.templ.inits,
      self = defnObject.templ.self,
      statResults = statResults
    )
}
