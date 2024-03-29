package io.github.effiban.scala2java.spi.transformers

import scala.meta.{Defn, Term}

/** A transformer which can transform a given Scala [[Term.ApplyInfix]] (infix-style method invocation), which appears in the scope of a Scala template
 * (class/object/trait body), into a [[Defn]] (definition of variable/method/class etc.).<br>
 * When the transformer returns a result, it means that besides the actual change - if it is in the scope of a class,
 * then the resulting definition will be placed in the Java class body, rather than in the Java constructor body.
 * <p>
 * A typical use-case would be to rewrite a Scala method invocation into an annotated Java definition,
 * which is then invoked by reflection at runtime.<br>
 * For example, the ScalaTest 'FlatSpec' style defines a test by an infix '''invocation''' in the format:
 * <pre>
 * "My object" should "do something" {
 *     // ...
 * }
 * </pre>
 * The equivalent in JUnit would be a method '''definition''' with the '@Test' annotation.
 */
trait TemplateTermApplyInfixToDefnTransformer extends DifferentTypeTransformer0[Term.ApplyInfix, Defn]

object TemplateTermApplyInfixToDefnTransformer {

  /** The default transformer which returns empty, indicating that no transformation is needed. */
  val Empty: TemplateTermApplyInfixToDefnTransformer = _ => None
}
