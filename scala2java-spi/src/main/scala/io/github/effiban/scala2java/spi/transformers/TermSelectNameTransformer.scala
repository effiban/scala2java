package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext

import scala.meta.Term

/** A transformer which can convert the <b>name</b> part of a given Scala [[Term.Select]] (qualified name) into a Java equivalent.<br>
 * This transformer should be overriden whenever the qualifier and name can be transformed separately, since the
 * transformation of the name part depends only the type of the qualifier, while the qualifier itself can be transformed independently.<br>
 * The transformer receives a context object which includes the type of the qualifier (when available).<br>
 */
trait TermSelectNameTransformer extends SameTypeTransformer1[Term.Name, TermSelectTransformationContext]

object TermSelectNameTransformer {
  /** The default transformer which returns the same [[Term.Name]] , indicating that no transformation is needed. */
  val Identity: TermSelectNameTransformer = (name, _) => name
}

