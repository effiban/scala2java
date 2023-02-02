package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

/** A transformer which can transform a given [[Term.ApplyInfix]] (an infix method invocation e.g.: `a doSomething b`)
 * into a [[Term.Apply]] (a regular method invocation).
 *
 * This can be useful when translating code from a Scala framework that uses infix invocations, to the equivalent in Java
 * which naturally uses regular method invocations.<br>
 * For example, given a ScalaTest assertion such as: `x shouldBe y`, the JUnit equivalent would be: `assertThat(x, is(y))`
 */
trait TermApplyInfixToTermApplyTransformer extends DifferentTypeTransformer0[Term.ApplyInfix, Term.Apply]

object TermApplyInfixToTermApplyTransformer {
  val Empty: TermApplyInfixToTermApplyTransformer = _ => None
}
