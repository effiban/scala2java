package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.entities.TermNameValues
import io.github.effiban.scala2java.core.renderers.contexts.{ArrayInitializerSizeRenderContext, ArrayInitializerValuesRenderContext}

import scala.meta.{Init, Lit, Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

trait ArrayInitializerRenderContextResolver {

  def tryResolve(termApply: Term.Apply): Option[ArrayInitializerValuesRenderContext]

  def tryResolve(init: Init): Option[ArrayInitializerSizeRenderContext]
}

object ArrayInitializerRenderContextResolver extends ArrayInitializerRenderContextResolver {

  override def tryResolve(termApply: Term.Apply): Option[ArrayInitializerValuesRenderContext] = {
    termApply.fun match {
      case arrayInitializer@(q"scala.Array" | Term.Select(q"scala.Array", Term.Name(TermNameValues.Apply))) =>
        throw new IllegalStateException(
          s"An array values initializer must be typed by the time this resolver is called, but it is: $arrayInitializer")
      case Term.ApplyType(q"scala.Array", tpe :: Nil) =>
        Some(ArrayInitializerValuesRenderContext(tpe = tpe, values = termApply.args))
      case Term.ApplyType(Term.Select(q"scala.Array", Term.Name(TermNameValues.Apply)), tpe :: Nil) =>
        Some(ArrayInitializerValuesRenderContext(tpe = tpe, values = termApply.args))

        // TODO remove once term names are fully qualified at start
      case arrayInitializer@(Term.Name(TermNameValues.ScalaArray) |
                             Term.Select(Term.Name(TermNameValues.ScalaArray), Term.Name(TermNameValues.Apply))) =>
        throw new IllegalStateException(
          s"An array values initializer must be typed by the time this resolver is called, but it is: $arrayInitializer")
      case Term.ApplyType(Term.Name(TermNameValues.ScalaArray), tpe :: Nil) =>
        Some(ArrayInitializerValuesRenderContext(tpe = tpe, values = termApply.args))
      case Term.ApplyType(Term.Select(Term.Name(TermNameValues.ScalaArray), Term.Name(TermNameValues.Apply)), tpe :: Nil) =>
        Some(ArrayInitializerValuesRenderContext(tpe = tpe, values = termApply.args))

      case _ => None
    }
  }

  override def tryResolve(init: Init): Option[ArrayInitializerSizeRenderContext] = {
    init.tpe match {
      case t"scala.Array" =>
        Some(ArrayInitializerSizeRenderContext(tpe = t"Object", size = resolveSize(init.argss)))
      case Type.Apply(t"scala.Array", tpe :: Nil) =>
        Some(ArrayInitializerSizeRenderContext(tpe = tpe, size = resolveSize(init.argss)))
      case _ => None
    }
  }

  private def resolveSize(argss: List[List[Term]]): Term = {
    argss match {
      case List(arg :: Nil) => arg
      case _ => Lit.Int(0)
    }
  }
}
