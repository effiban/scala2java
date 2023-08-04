package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedDefnScalatestMatcher.equalEnrichedDefn
import io.github.effiban.scala2java.core.enrichers.entities._
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.PrimaryCtors
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnEnricherImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.Class)

  private val defnVarEnricher = mock[DefnVarEnricher]
  private val defnDefEnricher = mock[DefnDefEnricher]
  private val traitEnricher = mock[TraitEnricher]
  private val classEnricher = mock[ClassEnricher]
  private val objectEnricher = mock[ObjectEnricher]

  private val defnEnricher = new DefnEnricherImpl(
    defnVarEnricher,
    defnDefEnricher,
    traitEnricher,
    classEnricher,
    objectEnricher
  )

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

  test("enrich() a Defn.Trait") {
    val defnTrait = q"trait MyTrait { def myMethod(param1: Int, param2: Int): String = param1 + param2 }"
    val enrichedTrait = EnrichedTrait(
      name = t"MyTrait",
      enrichedStats = List(EnrichedDefnDef(q"def myMethod(param1: Int, param2: Int): String = param1 + param2"))
    )

    doReturn(enrichedTrait).when(traitEnricher).enrich(eqTree(defnTrait), eqTo(TheStatContext))

    defnEnricher.enrich(defnTrait, TheStatContext) should equalEnrichedDefn(enrichedTrait)
  }

  test("enrich() a Defn.Class") {
    val defnClass = q"class MyClass { def myMethod(param1: Int, param2: Int): String = param1 + param2 }"
    val enrichedClass = EnrichedRegularClass(
      name = t"MyClass",
      ctor = PrimaryCtors.Empty,
      enrichedStats = List(EnrichedDefnDef(q"def myMethod(param1: Int, param2: Int): String = param1 + param2"))
    )

    doReturn(enrichedClass).when(classEnricher).enrich(eqTree(defnClass), eqTo(TheStatContext))

    defnEnricher.enrich(defnClass, TheStatContext) should equalEnrichedDefn(enrichedClass)
  }

  test("enrich() a Defn.Object") {
    val defnObject = q"object MyObject { def myMethod(param1: Int, param2: Int): String = param1 + param2 }"
    val enrichedObject = EnrichedObject(
      name = q"MyObject",
      enrichedStats = List(EnrichedDefnDef(q"def myMethod(param1: Int, param2: Int): String = param1 + param2"))
    )

    doReturn(enrichedObject).when(objectEnricher).enrich(eqTree(defnObject), eqTo(TheStatContext))

    defnEnricher.enrich(defnObject, TheStatContext) should equalEnrichedDefn(enrichedObject)
  }
}
