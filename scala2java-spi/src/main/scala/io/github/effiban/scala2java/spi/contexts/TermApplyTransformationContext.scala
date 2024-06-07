package io.github.effiban.scala2java.spi.contexts

import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.Type

/** Holds a context with information which helps to determine how to transform a [[scala.meta.Term.Apply]] (method invocation)
 * to a Java equivalent.<br>
 *
 * @param maybeQualifierType the inferred type of the method qualifier, if available
 * @param partialDeclDef the inferred partial method declaration, as much as available
 */
case class TermApplyTransformationContext(maybeQualifierType: Option[Type] = None,
                                          partialDeclDef: PartialDeclDef = PartialDeclDef())
