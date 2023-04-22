package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

/** A traverser decorator, overriding the default behavior for terms which may be evaluated as a 'fun' (function or method name).<br>
 * For such terms we need to either enable or disable the ability to 'desugar' into a method invocation - according to the context.<br>
 * This is because in the wrong context, it could either cause an invalid expression or else an infinite recursion.
 */
private[traversers] class FunOverridingTermTraverser(termRefTraverser: => ExpressionTermRefTraverser,
                                                     applyTypeTraverser: => MainApplyTypeTraverser,
                                                     termTraverser: => TermTraverser) extends TermTraverser {

  override def traverse(fun: Term): Unit = fun match {
    case ref: Term.Ref => termRefTraverser.traverse(ref)
    case applyType: Term.ApplyType => applyTypeTraverser.traverse(applyType)
    case term => termTraverser.traverse(term)
  }
}
