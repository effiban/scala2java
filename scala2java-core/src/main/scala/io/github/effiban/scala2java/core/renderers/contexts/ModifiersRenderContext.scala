package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Mod

case class ModifiersRenderContext(scalaMods: List[Mod] = Nil,
                                  annotsOnSameLine: Boolean = false,
                                  javaModifiers: List[JavaModifier] = Nil)
