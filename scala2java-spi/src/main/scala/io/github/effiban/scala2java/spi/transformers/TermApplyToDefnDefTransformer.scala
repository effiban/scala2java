package io.github.effiban.scala2java.spi.transformers

import scala.meta.{Defn, Term}

/** A transformer which can transform a given Scala [[Term.Apply]] (method invocation) into a [[Defn.Def]] (method definition).<br>
 * It can useful for extensions that need to rewrite a Scala method invocation in to a Java method definition that is annotated
 * and then invoked by reflection at runtime.
 * <p>
 * For example, the ScalaTest 'FunSuite' style defines a test by invoking the 'test()' method.<br>
 * The equivalent in JUnit is a method definition with the '@Test' annotation.
 */
trait TermApplyToDefnDefTransformer extends DifferentTypeTransformer0[Term.Apply, Defn.Def]

object TermApplyToDefnDefTransformer {

  /** The default transformer which returns empty, indicating that no transformation is needed. */
  val Empty: TermApplyToDefnDefTransformer = _ => None
}
