package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedDefnScalatestMatcher.equalEnrichedDefn
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDefnDef, EnrichedDefnVar}
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DefnEnricherImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.Class)

  private val defnVarEnricher = mock[DefnVarEnricher]
  private val defnDefEnricher = mock[DefnDefEnricher]

  private val defnEnricher = new DefnEnricherImpl(defnVarEnricher, defnDefEnricher)

  test("enrich() a Defn.Var") {
    val defnVar = q"private var myVar: Int = 3"
    val javaModifiers = List(JavaModifier.Private)
    val enrichedDefnVar = EnrichedDefnVar(defnVar, javaModifiers)

    doReturn(enrichedDefnVar).when(defnVarEnricher).enrich(eqTree(defnVar), eqTo(TheStatContext))

    defnEnricher.enrich(defnVar, TheStatContext) should equalEnrichedDefn(enrichedDefnVar)
  }

  test("enrich() a Defn.Def") {
    val defnDef = q"private def myMethod(param1: Int, param2: Int): String = param1 + param2"
    val javaModifiers = List(JavaModifier.Private)
    val enrichedDefnDef = EnrichedDefnDef(defnDef, javaModifiers)

    doReturn(enrichedDefnDef).when(defnDefEnricher).enrich(eqTree(defnDef), eqTo(TheStatContext))

    defnEnricher.enrich(defnDef, TheStatContext) should equalEnrichedDefn(enrichedDefnDef)
  }
}
