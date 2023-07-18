package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.Name

case class RegularClassRenderContext(javaModifiers: List[JavaModifier] = Nil,
                                     javaTypeKeyword: JavaKeyword = JavaKeyword.Class,
                                     maybeInheritanceKeyword: Option[JavaKeyword] = None,
                                     permittedSubTypeNames: List[Name] = Nil,
                                     bodyContext: TemplateBodyRenderContext = TemplateBodyRenderContext())
