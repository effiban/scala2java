package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaModifier
case class ValOrVarRenderContext(javaModifiers: List[JavaModifier] = Nil, inBlock: Boolean = false)
