package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.JavaModifier
case class VarRenderContext(javaModifiers: List[JavaModifier] = Nil, inBlock: Boolean = false) extends DefnRenderContext
