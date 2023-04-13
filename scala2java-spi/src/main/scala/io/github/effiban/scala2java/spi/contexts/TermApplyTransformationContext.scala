package io.github.effiban.scala2java.spi.contexts

import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.Type

/** Holds a context with information which is relevant for transforming a [[scala.meta.Term.Apply]] (method invocation) to Java style.<br>
 *
 * @param maybeParentType the inferred type of the parent (e.g. class/trait) of the method, if available
 * @param partialDeclDef the inferred partial method declaration, as much as available
 */
case class TermApplyTransformationContext(maybeParentType: Option[Type] = None,
                                          partialDeclDef: PartialDeclDef = PartialDeclDef())
