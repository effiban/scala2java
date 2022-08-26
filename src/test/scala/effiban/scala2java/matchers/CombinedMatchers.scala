package effiban.scala2java.matchers

import org.mockito.ArgumentMatchers.argThat

import scala.meta.Tree

object CombinedMatchers {

  def eqSomeTree[T <: Tree](expectedTree: T): Option[T] =
    argThat(new SomeMatcher[T](expectedTree, new TreeMatcher[T](_)))

  def eqTreeList[T <: Tree](expected: List[T]): List[T] = argThat(new ListMatcher(expected, new TreeMatcher[T](_)))

  def eqOptionTreeList[T <: Tree](expected: List[Option[T]]): List[Option[T]] =
    argThat(new ListMatcher(expected, new OptionMatcher[T](_, new TreeMatcher[T](_))))
}