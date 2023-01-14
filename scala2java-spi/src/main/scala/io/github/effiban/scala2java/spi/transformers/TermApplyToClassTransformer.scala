package io.github.effiban.scala2java.spi.transformers

import scala.meta.{Defn, Term}

/** A transformer which can transform a given Scala [[Term.Apply]] (method invocation) into a [[Defn.Class]].<br>
 * It can useful for extensions that need to rewrite a Scala method invocation into a Java class definition that is annotated
 * and then invoked by reflection at runtime.
 * <p>
 * For example, in the ScalaTest 'FunSpec' style, nested tests can be defined by nested invocations to 'describe()'.<br>
 * The equivalents in JUnit are nested inner classes with the '@Nested' annotation, having '@Test' annotated methods inside.
 */
trait TermApplyToClassTransformer extends DifferentTypeTransformer0[Term.Apply, Defn.Class]

object TermApplyToClassTransformer {

  /** The default transformer which returns empty, indicating that no transformation is needed. */
  val Empty: TermApplyToClassTransformer = _ => None
}
