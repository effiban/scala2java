package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaKeyword

import scala.meta.{Init, Name, Self, Template}

case class TemplateTraversalResult(maybeInheritanceKeyword: Option[JavaKeyword] = None,
                                   inits: List[Init] = Nil,
                                   self: Self = Self(Name.Anonymous(), None),
                                   statResults: List[PopulatedStatTraversalResult] = Nil) {
  val template: Template = Template(
    early = Nil,
    inits = inits,
    self = self,
    stats = statResults.map(_.tree)
  )
}
