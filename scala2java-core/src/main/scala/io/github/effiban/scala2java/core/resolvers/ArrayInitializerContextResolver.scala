package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerSizeContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.entities.{TermNameValues, TypeNameValues}

import scala.meta.{Init, Lit, Term, Type, XtensionQuasiquoteTerm}

trait ArrayInitializerContextResolver {

  def tryResolve(termApply: Term.Apply): Option[ArrayInitializerValuesContext]

  def tryResolve(init: Init): Option[ArrayInitializerSizeContext]
}

object ArrayInitializerContextResolver extends ArrayInitializerContextResolver {

  override def tryResolve(termApply: Term.Apply): Option[ArrayInitializerValuesContext] = {
    termApply.fun match {
      case q"scala.Array" | Term.Select(q"scala.Array", Term.Name(TermNameValues.Apply)) =>
        Some(ArrayInitializerValuesContext(values = termApply.args))
      case Term.ApplyType(q"scala.Array", tpe :: Nil) =>
        Some(ArrayInitializerValuesContext(maybeType = Some(tpe), values = termApply.args))
      case Term.ApplyType(Term.Select(q"scala.Array", Term.Name(TermNameValues.Apply)), tpe :: Nil) =>
        Some(ArrayInitializerValuesContext(maybeType = Some(tpe), values = termApply.args))

      // TODO remove the following block of cases once term names are fully qualified at start
      case Term.Name(TermNameValues.ScalaArray) |
           Term.Select(Term.Name(TermNameValues.ScalaArray), Term.Name(TermNameValues.Apply)) =>
        Some(ArrayInitializerValuesContext(values = termApply.args))
      case Term.ApplyType(Term.Name(TermNameValues.ScalaArray), tpe :: Nil) =>
        Some(ArrayInitializerValuesContext(maybeType = Some(tpe), values = termApply.args))
      case Term.ApplyType(Term.Select(Term.Name(TermNameValues.ScalaArray), Term.Name(TermNameValues.Apply)), tpe :: Nil) =>
        Some(ArrayInitializerValuesContext(maybeType = Some(tpe), values = termApply.args))
      case _ => None
    }
  }

  override def tryResolve(init: Init): Option[ArrayInitializerSizeContext] = {
    init.tpe match {
      case Type.Name(TypeNameValues.ScalaArray) => Some(ArrayInitializerSizeContext(size = resolveSize(init.argss)))
      case Type.Apply(Type.Name(TypeNameValues.ScalaArray), tpe :: Nil) =>
        Some(ArrayInitializerSizeContext(tpe = tpe, size = resolveSize(init.argss)))
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
