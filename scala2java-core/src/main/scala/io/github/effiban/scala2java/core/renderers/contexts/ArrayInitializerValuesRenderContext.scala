package io.github.effiban.scala2java.core.renderers.contexts

import scala.meta.{Term, Type, XtensionQuasiquoteType}

case class ArrayInitializerValuesRenderContext(tpe: Type = t"Object", values: List[Term] = Nil)
