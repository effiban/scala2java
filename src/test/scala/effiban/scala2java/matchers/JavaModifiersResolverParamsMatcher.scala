package effiban.scala2java.matchers

import effiban.scala2java.resolvers.JavaModifiersResolverParams
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Mod

class JavaModifiersResolverParamsMatcher(expectedParams: JavaModifiersResolverParams) extends ArgumentMatcher[JavaModifiersResolverParams] {

  override def matches(actualParams: JavaModifiersResolverParams): Boolean = {
    scalaTreeMatches(actualParams) &&
      scalaModsMatch(actualParams) &&
      actualParams.javaTreeType == expectedParams.javaTreeType
      actualParams.javaScope == expectedParams.javaScope
  }

  private def scalaTreeMatches(actualParams: JavaModifiersResolverParams) = {
    new TreeMatcher(expectedParams.scalaTree).matches(actualParams.scalaTree)
  }

  private def scalaModsMatch(actualParams: JavaModifiersResolverParams): Boolean = {
    new ListMatcher(expectedParams.scalaMods, new TreeMatcher[Mod](_)).matches(actualParams.scalaMods)
  }

  override def toString: String = s"Matcher for: $expectedParams"
}

object JavaModifiersResolverParamsMatcher {
  def eqJavaModifiersResolverParams(expectedParams: JavaModifiersResolverParams): JavaModifiersResolverParams =
    argThat(new JavaModifiersResolverParamsMatcher(expectedParams))
}

