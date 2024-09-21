package io.github.effiban.scala2java.test.utils.matchers

import io.github.effiban.scala2java.spi.entities.QualifiedTermApply
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class QualifiedTermApplyMockitoMatcher(expectedQualifiedTermApply: QualifiedTermApply)
  extends ArgumentMatcher[QualifiedTermApply] {

  override def matches(actualQualifiedTermApply: QualifiedTermApply): Boolean = {
    qualifiedNameMatches(actualQualifiedTermApply) &&
      typeArgsMatch(actualQualifiedTermApply) &&
      argsMatch(actualQualifiedTermApply)

  }

  override def toString: String = s"Matcher for: $expectedQualifiedTermApply"

  private def qualifiedNameMatches(actualQualifiedTermApply: QualifiedTermApply) = {
    actualQualifiedTermApply.qualifiedName.structure == expectedQualifiedTermApply.qualifiedName.structure
  }

  private def typeArgsMatch(actualQualifiedTermApply : QualifiedTermApply) = {
    actualQualifiedTermApply.typeArgs.structure == expectedQualifiedTermApply.typeArgs.structure
  }

  private def argsMatch(actualQualifiedTermApply : QualifiedTermApply) = {
    actualQualifiedTermApply.args.structure == expectedQualifiedTermApply.args.structure
  }
}

object QualifiedTermApplyMockitoMatcher {
  def eqQualifiedTermApply(expectedQualifiedTermApply: QualifiedTermApply): QualifiedTermApply =
    argThat(new QualifiedTermApplyMockitoMatcher(expectedQualifiedTermApply))
}

