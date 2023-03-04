package io.github.effiban.scala2java.core.contexts

import scala.meta.{Init, Stat, Term}

case class CtorRequiredResolutionContext(inits: List[Init] = Nil,
                                         terms: List[Term] = Nil,
                                         otherStats: List[Stat] = Nil)
