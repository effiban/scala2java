package io.github.effiban.scala2java.test.utils.matchers

import io.github.effiban.scala2java.spi.entities.UnqualifiedTermApply
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Term, Type}

class UnqualifiedTermApplyMockitoMatcher(expectedUnqualifiedTermApply: UnqualifiedTermApply)
  extends ArgumentMatcher[UnqualifiedTermApply] {

  override def matches(actualUnqualifiedTermApply: UnqualifiedTermApply): Boolean = {
    nameMatches(actualUnqualifiedTermApply) &&
      typeArgsMatch(actualUnqualifiedTermApply) &&
      argsMatch(actualUnqualifiedTermApply)

  }

  override def toString: String = s"Matcher for: $expectedUnqualifiedTermApply"

  private def nameMatches(actualUnqualifiedTermApply: UnqualifiedTermApply) = {
    actualUnqualifiedTermApply.name.structure == expectedUnqualifiedTermApply.name.structure
  }

  private def typeArgsMatch(actualUnqualifiedTermApply : UnqualifiedTermApply) = {
    actualUnqualifiedTermApply.typeArgs.structure == expectedUnqualifiedTermApply.typeArgs.structure
  }

  private def argsMatch(actualUnqualifiedTermApply : UnqualifiedTermApply) = {
    actualUnqualifiedTermApply.args.structure == expectedUnqualifiedTermApply.args.structure
  }
}

object UnqualifiedTermApplyMockitoMatcher {
  def eqUnqualifiedTermApply(expectedUnqualifiedTermApply: UnqualifiedTermApply): UnqualifiedTermApply =
    argThat(new UnqualifiedTermApplyMockitoMatcher(expectedUnqualifiedTermApply))
}

