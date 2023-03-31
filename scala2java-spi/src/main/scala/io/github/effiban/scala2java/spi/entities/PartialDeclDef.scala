package io.github.effiban.scala2java.spi.entities

import scala.meta.{Term, Type}

/** Holds a partial [[scala.meta.Decl.Def]] (method declaration), containing information which is typically inferred
 * from a method invocation (a [[scala.meta.Term.Apply]]).<br>
 * The method name and type are not included, since they do not require inference and will be provided separately to the client code.
 *
 * @param maybeParamNames the inferred names of the method parameters, whichever available
 * @param maybeParamTypes the inferred types of the method parameters, whichever available
 * @param maybeReturnType the inferred return type, if available
 */
case class PartialDeclDef(maybeParamNames: List[Option[Term.Name]] = Nil,
                          maybeParamTypes: List[Option[Type]] = Nil,
                          maybeReturnType: Option[Type] = None) {
  def isEmpty: Boolean = {
      maybeParamNames.isEmpty &&
      maybeParamTypes.isEmpty &&
      maybeReturnType.isEmpty
  }

  def nonEmpty: Boolean = !isEmpty
}
