package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Mod.Annot

case class ModifiersRenderContext(annots: List[Annot] = Nil,
                                  annotsOnSameLine: Boolean = false,
                                  hasImplicit: Boolean = false,
                                  javaModifiers: List[JavaModifier] = Nil)
