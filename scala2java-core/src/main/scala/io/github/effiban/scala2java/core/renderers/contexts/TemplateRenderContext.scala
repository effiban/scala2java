package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.JavaKeyword

import scala.meta.Name

case class TemplateRenderContext(maybeInheritanceKeyword: Option[JavaKeyword] = None,
                                 permittedSubTypeNames: List[Name] = Nil,
                                 bodyContext: TemplateBodyRenderContext = TemplateBodyRenderContext())
