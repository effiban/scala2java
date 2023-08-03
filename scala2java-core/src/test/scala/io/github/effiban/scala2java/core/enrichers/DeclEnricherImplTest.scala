package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedDeclScalatestMatcher.equalEnrichedDecl
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDeclDef, EnrichedDeclVar}
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DeclEnricherImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.Class)

  private val declVarEnricher = mock[DeclVarEnricher]
  private val declDefEnricher = mock[DeclDefEnricher]

  private val declEnricher = new DeclEnricherImpl(declVarEnricher, declDefEnricher)

  test("enrich() a Decl.Var") {
    val declVar = q"private var myVar: Int"
    val javaModifiers = List(JavaModifier.Private)
    val enrichedDeclVar = EnrichedDeclVar(declVar, javaModifiers)

    doReturn(enrichedDeclVar).when(declVarEnricher).enrich(eqTree(declVar), eqTo(TheStatContext))

    declEnricher.enrich(declVar, TheStatContext) should equalEnrichedDecl(enrichedDeclVar)
  }

  test("enrich() a Decl.Def") {
    val declDef = q"private def myMethod(param1: Int, param2: Int): String"
    val javaModifiers = List(JavaModifier.Private)
    val enrichedDeclDef = EnrichedDeclDef(declDef, javaModifiers)

    doReturn(enrichedDeclDef).when(declDefEnricher).enrich(eqTree(declDef), eqTo(TheStatContext))

    declEnricher.enrich(declDef, TheStatContext) should equalEnrichedDecl(enrichedDeclDef)
  }
}
