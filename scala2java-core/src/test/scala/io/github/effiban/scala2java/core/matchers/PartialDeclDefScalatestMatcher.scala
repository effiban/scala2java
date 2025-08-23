package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, OptionMatcher, TreeMatcher}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.meta.{Term, Type}

class PartialDeclDefScalatestMatcher(expectedPartialDeclDef: PartialDeclDef) extends Matcher[PartialDeclDef] {

  override def apply(actualPartialDeclDef: PartialDeclDef): MatchResult = {
    val matches = maybeParamNamesMatch(actualPartialDeclDef) &&
      maybeParamTypesMatch(actualPartialDeclDef) &&
      maybeReturnTypesMatch(actualPartialDeclDef)

    MatchResult(matches,
      s"Actual PartialDeclDef: $actualPartialDeclDef is NOT the same as expected PartialDeclDef: $expectedPartialDeclDef",
      s"Actual PartialDeclDef: $actualPartialDeclDef the same as expected PartialDeclDef: $expectedPartialDeclDef"
    )

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

object PartialDeclDefScalatestMatcher {
  def equalPartialDeclDef(expectedPartialDeclDef: PartialDeclDef): Matcher[PartialDeclDef] =
    new PartialDeclDefScalatestMatcher(expectedPartialDeclDef)
}

