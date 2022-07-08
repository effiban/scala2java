package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder

import scala.meta.Term
import scala.meta.Term.{AnonymousFunction, Param}

trait AnonymousFunctionTraverser extends ScalaTreeTraverser[AnonymousFunction]

private[scala2java] class AnonymousFunctionTraverserImpl(termFunctionTraverser: => TermFunctionTraverser) extends AnonymousFunctionTraverser {

  override def traverse(anonymousFunction: AnonymousFunction): Unit = {
    termFunctionTraverser.traverse(Term.Function(
      params = List(Param(name = Term.Name(JavaPlaceholder), mods = Nil, decltpe = None, default = None)),
      body = anonymousFunction.body))
  }
}

object AnonymousFunctionTraverser extends AnonymousFunctionTraverserImpl(TermFunctionTraverser)
