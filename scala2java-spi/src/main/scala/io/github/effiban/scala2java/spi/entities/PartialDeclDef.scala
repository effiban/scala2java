package io.github.effiban.scala2java.spi.entities

import scala.meta.{Term, Type}

/** Holds a partial [[scala.meta.Decl.Def]] (method declaration), containing information which is typically inferred
 * from a method invocation (a [[scala.meta.Term.Apply]]).<br>
 * The method name and type are not included, since they do not require inference and will be provided separately to the client code.
 *
 * @param maybeParamNameLists the inferred names of the method parameters multi-lists, whichever available
 * @param maybeParamTypeLists the inferred types of the method parameters multi-lists, whichever available
 * @param maybeReturnType the inferred return type, if available
 */
case class PartialDeclDef(maybeParamNameLists: List[List[Option[Term.Name]]] = Nil,
                          maybeParamTypeLists: List[List[Option[Type]]] = Nil,
                          maybeReturnType: Option[Type] = None) {
  def isEmpty: Boolean = {
      maybeParamNameLists.flatten.isEmpty &&
      maybeParamTypeLists.flatten.isEmpty &&
      maybeReturnType.isEmpty
  }

  def nonEmpty: Boolean = !isEmpty
}
