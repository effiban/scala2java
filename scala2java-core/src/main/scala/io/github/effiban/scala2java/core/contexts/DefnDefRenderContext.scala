package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaModifier

case class DefnDefRenderContext(methodJavaModifiers: List[JavaModifier] = Nil,
                                paramJavaModifiers: List[JavaModifier] = Nil)
