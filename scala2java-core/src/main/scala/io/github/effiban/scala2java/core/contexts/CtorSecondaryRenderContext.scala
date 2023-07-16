package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Type

case class CtorSecondaryRenderContext(className: Type.Name, javaModifiers: List[JavaModifier] = Nil)
