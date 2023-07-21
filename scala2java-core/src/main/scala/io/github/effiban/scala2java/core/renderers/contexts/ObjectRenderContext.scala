package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

case class ObjectRenderContext(javaModifiers: List[JavaModifier] = Nil,
                               javaTypeKeyword: JavaKeyword = JavaKeyword.Class,
                               maybeInheritanceKeyword: Option[JavaKeyword] = None,
                               bodyContext: TemplateBodyRenderContext = TemplateBodyRenderContext()) extends DefnRenderContext
