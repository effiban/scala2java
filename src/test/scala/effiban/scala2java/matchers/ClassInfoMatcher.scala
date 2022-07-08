package effiban.scala2java.matchers

import effiban.scala2java.entities.ClassInfo
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class ClassInfoMatcher(expectedClassInfo: ClassInfo) extends ArgumentMatcher[ClassInfo] {

  override def matches(actualClassInfo: ClassInfo): Boolean = {
    classNameMatches(actualClassInfo) && explicitPrimaryCtorMatches(actualClassInfo)
  }

  private def classNameMatches(actualClassInfo: ClassInfo) = {
    new TreeMatcher(expectedClassInfo.className).matches(actualClassInfo.className)
  }

  private def explicitPrimaryCtorMatches(actualClassInfo: ClassInfo) = {
    (actualClassInfo.maybePrimaryCtor, expectedClassInfo.maybePrimaryCtor) match {
      case (Some(actualPrimaryCtor), Some(expectedPrimaryCtor)) => new TreeMatcher(expectedPrimaryCtor).matches(actualPrimaryCtor)
      case (None, None) => true
      case _ => false
    }
  }

  override def toString: String = s"Matcher for: $expectedClassInfo"
}

object ClassInfoMatcher {
  def eqClassInfo(expectedClassInfo: ClassInfo): ClassInfo = argThat(new ClassInfoMatcher(expectedClassInfo))
}

