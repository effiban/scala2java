package effiban.scala2java.matchers

import org.mockito.ArgumentMatchers.argThat

import scala.meta.Tree

object CombinedMatchers {

  def eqSomeTree[T <: Tree](expectedTree: T): Option[T] =
    argThat(new SomeMatcher[T](expectedTree, new TreeMatcher[T](_)))
}
