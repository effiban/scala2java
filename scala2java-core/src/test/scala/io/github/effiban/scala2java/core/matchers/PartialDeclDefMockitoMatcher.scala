package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Term, Type}

class PartialDeclDefMockitoMatcher(expectedPartialDeclDef: PartialDeclDef) extends ArgumentMatcher[PartialDeclDef] {

  override def matches(actualPartialDeclDef: PartialDeclDef): Boolean = {
      maybeParamNamesMatch(actualPartialDeclDef) &&
      maybeParamTypesMatch(actualPartialDeclDef) &&
      maybeReturnTypesMatch(actualPartialDeclDef)
  }

  override def toString: String = s"Matcher for: $expectedPartialDeclDef"

  private def maybeParamNamesMatch(actualPartialDeclDef: PartialDeclDef): Boolean = {
    val expectedMaybeParamNameLists = expectedPartialDeclDef.maybeParamNameLists
    val actualMaybeParamNameLists = actualPartialDeclDef.maybeParamNameLists

    expectedMaybeParamNameLists.size == actualMaybeParamNameLists.size &&
      expectedMaybeParamNameLists.zipWithIndex.forall { case (expectedMaybeParamNameList, index) =>
        new ListMatcher(expectedMaybeParamNameList, new OptionMatcher[Term.Name](_, new TreeMatcher[Term.Name](_)))
          .matches(actualPartialDeclDef.maybeParamNameLists(index))
      }
  }

  private def maybeParamTypesMatch(actualPartialDeclDef: PartialDeclDef): Boolean = {
    val expectedMaybeParamTypeLists = expectedPartialDeclDef.maybeParamTypeLists
    val actualMaybeParamTypeLists = actualPartialDeclDef.maybeParamTypeLists

    expectedMaybeParamTypeLists.size == actualMaybeParamTypeLists.size &&
      expectedMaybeParamTypeLists.zipWithIndex.forall { case (expectedMaybeParamTypeList, index) =>
        new ListMatcher(expectedMaybeParamTypeList, new OptionMatcher[Type](_, new TreeMatcher[Type](_)))
          .matches(actualPartialDeclDef.maybeParamTypeLists(index))
      }
  }

  private def maybeReturnTypesMatch(actualPartialDeclDef: PartialDeclDef): Boolean = {
    new OptionMatcher(expectedPartialDeclDef.maybeReturnType, new TreeMatcher[Type](_)).matches(actualPartialDeclDef.maybeReturnType)
  }
}

object PartialDeclDefMockitoMatcher {
  def eqPartialDeclDef(expectedSignature: PartialDeclDef): PartialDeclDef =
    argThat(new PartialDeclDefMockitoMatcher(expectedSignature))
}

