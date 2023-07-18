package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

case class CaseClassRenderContext(javaModifiers: List[JavaModifier] = Nil,
                                  maybeInheritanceKeyword: Option[JavaKeyword] = None,
                                  bodyContext: TemplateBodyRenderContext = TemplateBodyRenderContext())
