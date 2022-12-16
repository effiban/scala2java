package io.github.effiban.scala2java.test.utils.matchers

import org.mockito.ArgumentMatchers.argThat

import scala.meta.Tree

/** Convenience reporter methods (using `argThat`) for Mockito [[ArgumentMatcher]]-s,
 *  which combine other matchers from this package
 */
object CombinedMatchers {

  /** Checks if the given expected tree is equal to the actual one wrapped with `Some`<br>
   *
   * @param expectedTree the expected tree to match
   */
  def eqSomeTree[T <: Tree](expectedTree: T): Option[T] =
    argThat(new SomeMatcher[T](expectedTree, new TreeMatcher[T](_)))

  /** Checks if the given expected optional tree is equal to the actual one
   *
   * @param expected the expected optional tree to match
   */
  def eqOptionTree[T <: Tree](expected: Option[T]): Option[T] =
    argThat(new OptionMatcher[T](expected, new TreeMatcher[T](_)))

  /** Checks if the given expected list of trees is equal to the actual one
   *
   * @param expected the expected list of trees to match
   */
  def eqTreeList[T <: Tree](expected: List[T]): List[T] = argThat(new ListMatcher(expected, new TreeMatcher[T](_)))

  /** Checks if the given expected list of optional trees is equal to the actual one
   *
   * @param expected the expected list of optional trees to match
   */
  def eqOptionTreeList[T <: Tree](expected: List[Option[T]]): List[Option[T]] =
    argThat(new ListMatcher(expected, new OptionMatcher[T](_, new TreeMatcher[T](_))))
}
