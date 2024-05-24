package io.github.effiban.scala2java.spi.contexts

import io.github.effiban.scala2java.spi.entities.PartialDeclDef

/** Holds a context with information which is relevant for transforming an qualified [[scala.meta.Term.Apply]] (method invocation)
 * to Java style.<br>
 * The context holds the inferred parts of the method declaration, as much as available
 *
 * @param partialDeclDef the inferred partial method declaration, as much as available
 */
case class QualifiedTermApplyTransformationContext(partialDeclDef: PartialDeclDef = PartialDeclDef())
