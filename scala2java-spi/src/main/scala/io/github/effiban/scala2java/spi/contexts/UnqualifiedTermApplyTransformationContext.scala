package io.github.effiban.scala2java.spi.contexts

import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.Type

/** Holds a context with information which is relevant for transforming an unqualified [[scala.meta.Term.Apply]] (method invocation)
 * to Java style.<br>
 * "Unqualified" here actually means that the method does has a qualifier, but it can be transformed separately -
 * and in order to transform the unqualified part of the method, we only need to know the qualifier type which is provided here.<br>
 * In addition, the inferred parts of the method signature are provided, if available.
 *
 * @param maybeQualifierType the inferred type of the method qualifier, if available
 * @param partialDeclDef the inferred partial method declaration, as much as available
 */
case class UnqualifiedTermApplyTransformationContext(maybeQualifierType: Option[Type] = None,
                                                     partialDeclDef: PartialDeclDef = PartialDeclDef()) {

  def asQualifiedContext(): QualifiedTermApplyTransformationContext = QualifiedTermApplyTransformationContext(partialDeclDef)
}
