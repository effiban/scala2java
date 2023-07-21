package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.JavaModifier

case class DeclRenderContext(javaModifiers: List[JavaModifier] = Nil) extends TemplateStatRenderContext
