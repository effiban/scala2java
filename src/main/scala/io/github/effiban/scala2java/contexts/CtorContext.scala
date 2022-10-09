package io.github.effiban.scala2java.contexts

import io.github.effiban.scala2java.entities.JavaScope.JavaScope

import scala.meta.{Init, Term, Type}

case class CtorContext(override val javaScope: JavaScope,
                       className: Type.Name,
                       inits: List[Init] = Nil,
                       terms: List[Term] = Nil) extends JavaScopeAware
