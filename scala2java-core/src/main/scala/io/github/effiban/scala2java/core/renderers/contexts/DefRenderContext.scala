package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.JavaModifier

case class DefRenderContext(javaModifiers: List[JavaModifier] = Nil) extends DefnRenderContext with DeclRenderContext
