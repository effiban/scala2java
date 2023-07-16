package io.github.effiban.scala2java.core.renderers.contexts

import scala.meta.Stat

case class TemplateBodyRenderContext(statContextMap: Map[Stat, TemplateStatRenderContext] = Map())
