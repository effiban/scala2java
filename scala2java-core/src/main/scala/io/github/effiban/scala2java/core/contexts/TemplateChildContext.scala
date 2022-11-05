package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaScope.JavaScope

import scala.meta.{Init, Term, Type}

case class TemplateChildContext(override val javaScope: JavaScope,
                                maybeClassName: Option[Type.Name] = None,
                                inits: List[Init] = Nil,
                                ctorTerms: List[Term] = Nil) extends JavaScopeAware
