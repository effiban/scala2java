package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities._
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedStatScalatestMatcher.equalEnrichedStat
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.Package
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DefaultStatEnricherImplTest extends UnitTestSuite {

  private val defnEnricher = mock[DefnEnricher]
  private val declEnricher = mock[DeclEnricher]

  private val defaultStatEnricher = new DefaultStatEnricherImpl(
    defnEnricher,
    declEnricher
  )

  test("enrich Term.Name") {
    val termName = q"myName"
    val enrichedTermName = EnrichedSimpleStat(termName)

    defaultStatEnricher.enrich(termName, StatContext(JavaScope.Class)) should equalEnrichedStat(enrichedTermName)
  }

  test("enrich Import") {
    val `import` = q"import somepackage1.SomeClass1"
    val enrichedImport = EnrichedSimpleStat(`import`)

    defaultStatEnricher.enrich(`import`, StatContext(Package)) should equalEnrichedStat(enrichedImport)
  }

  test("enrich Decl.Var") {
    val declVar = q"private var myVar: Int"
    val javaModifiers = List(JavaModifier.Private)
    val enrichedDeclVar = EnrichedDeclVar(declVar, javaModifiers)

    doReturn(enrichedDeclVar).when(declEnricher).enrich(eqTree(declVar), eqTo(StatContext(JavaScope.Block)))

    defaultStatEnricher.enrich(declVar, StatContext(JavaScope.Block)) should equalEnrichedStat(enrichedDeclVar)
  }

  test("enrich Decl.Def") {
    val declDef = q"private def foo(x: Int): Int"
    val javaModifiers = List(JavaModifier.Private)
    val enrichedDeclDef = EnrichedDeclDef(declDef, javaModifiers)

    doReturn(enrichedDeclDef).when(declEnricher).enrich(eqTree(declDef), eqTo(StatContext(JavaScope.Block)))

    defaultStatEnricher.enrich(declDef, StatContext(JavaScope.Block)) should equalEnrichedStat(enrichedDeclDef)
  }

  test("enrich Defn.Var") {
    val defnVar = q"private var myVar: Int = 3"
    val javaModifiers = List(JavaModifier.Private)
    val enriched = EnrichedDefnVar(defnVar, javaModifiers)

    doReturn(enriched).when(defnEnricher).enrich(eqTree(defnVar), eqTo(StatContext(JavaScope.Block)))

    defaultStatEnricher.enrich(defnVar, StatContext(JavaScope.Block)) should equalEnrichedStat(enriched)
  }

  test("enrich Defn.Def") {
    val defnDef = q"private def foo(x: Int): Int = doSomething(x)"
    val javaModifiers = List(JavaModifier.Private)
    val enriched = EnrichedDefnDef(defnDef, javaModifiers)

    doReturn(enriched).when(defnEnricher).enrich(eqTree(defnDef), eqTo(StatContext(JavaScope.Block)))

    defaultStatEnricher.enrich(defnDef, StatContext(JavaScope.Block)) should equalEnrichedStat(enriched)
  }
}
