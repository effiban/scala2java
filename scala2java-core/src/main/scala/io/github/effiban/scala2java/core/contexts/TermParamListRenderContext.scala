package io.github.effiban.scala2java.core.contexts

case class TermParamListRenderContext(paramContexts: List[TermParamRenderContext] = Nil,
                                      onSameLine: Boolean = false)
