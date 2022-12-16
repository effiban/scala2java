package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

/** A transformer which can transform a given [[Term.ApplyType]] (a type application such as: `identity[T]`)
 * into a [[Term.Apply]] (a method invocation).
 *
 * This can be useful when translating Scala code that uses a [[scala.reflect.ClassTag]].<br>
 * For example, in mockito-scala a mock object can be instantiated like this: `mock[Foo]`,
 * while the corresponding Java code requires a method invocation with the class object, like this:
 * `mock(Foo.class)`
 */
trait TermApplyTypeToTermApplyTransformer extends DifferentTypeTransformer[Term.ApplyType, Term.Apply]

object TermApplyTypeToTermApplyTransformer {
  /** The default transformer which returns `None`, indicating that no transformation is needed. */
  def Empty: TermApplyTypeToTermApplyTransformer = _ => None
}
