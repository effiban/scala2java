package io.github.effiban.scala2java.core.renderers.contexts

import scala.meta.{Lit, Term, Type, XtensionQuasiquoteType}

case class ArrayInitializerSizeRenderContext(tpe: Type = t"Object", size: Term = Lit.Int(0))
