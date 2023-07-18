package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Name

case class TraitRenderContext(javaModifiers: List[JavaModifier] = Nil,
                              permittedSubTypeNames: List[Name] = Nil,
                              bodyContext: TemplateBodyRenderContext = TemplateBodyRenderContext())
