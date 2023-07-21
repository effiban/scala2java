package io.github.effiban.scala2java.core.renderers.contexts

import scala.meta.Stat

case class SourceRenderContext(statContextMap: Map[Stat, StatRenderContext] = Map())
