package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerSizeRenderContext, ArrayInitializerValuesRenderContext}
import io.github.effiban.scala2java.core.entities.{TermNameValues, TypeNameValues}

import scala.meta.{Init, Lit, Term, Type}

trait ArrayInitializerRenderContextResolver {

  def tryResolve(termApply: Term.Apply): Option[ArrayInitializerValuesRenderContext]

  def tryResolve(init: Init): Option[ArrayInitializerSizeRenderContext]
}

object ArrayInitializerRenderContextResolver extends ArrayInitializerRenderContextResolver {

  override def tryResolve(termApply: Term.Apply): Option[ArrayInitializerValuesRenderContext] = {
    termApply.fun match {
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
      case arrayInitializer@Type.Name(TypeNameValues.ScalaArray) =>
        throw new IllegalStateException(
          s"An array size initializer must be typed by the time this resolver is called, but it is: $arrayInitializer")
      case Type.Apply(Type.Name(TypeNameValues.ScalaArray), tpe :: Nil) =>
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
