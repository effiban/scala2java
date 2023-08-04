package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.classifiers.ClassClassifier
import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedClassScalatestMatcher.equalEnrichedClass
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedCaseClass, EnrichedRegularClass}
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Templates}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Defn, Mod, Type, XtensionQuasiquoteType}

class ClassEnricherImplTest extends UnitTestSuite {

  private val caseClassEnricher = mock[CaseClassEnricher]
  private val regularClassEnricher = mock[RegularClassEnricher]
  private val classClassifier = mock[ClassClassifier]

  private val classEnricher = new ClassEnricherImpl(
    caseClassEnricher,
    regularClassEnricher,
    classClassifier
  )

  test("enrich when case class") {
    val classDef = classDefOf("MyClass", List(Mod.Case()))
    val expectedEnrichedCaseClass = EnrichedCaseClass(
      javaModifiers = List(JavaModifier.Public),
      name = t"MyClass",
      ctor = PrimaryCtors.Empty
    )

    when(classClassifier.isCase(eqTree(classDef))).thenReturn(true)
    doReturn(expectedEnrichedCaseClass)
      .when(caseClassEnricher).enrich(eqTree(classDef), eqTo(StatContext(JavaScope.Package)))

    classEnricher.enrich(classDef, StatContext(JavaScope.Package)) should equalEnrichedClass(expectedEnrichedCaseClass)
  }

  test("enrich when regular class") {
    val classDef = classDefOf("MyClass")
    val expectedEnrichedRegularClass = EnrichedRegularClass(
      javaModifiers = List(JavaModifier.Public),
      name = t"MyClass",
      ctor = PrimaryCtors.Empty
    )

    when(classClassifier.isCase(eqTree(classDef))).thenReturn(false)
    doReturn(expectedEnrichedRegularClass)
      .when(regularClassEnricher).enrich(eqTree(classDef), eqTo(StatContext(JavaScope.Package)))

    classEnricher.enrich(classDef, StatContext(JavaScope.Package)) should equalEnrichedClass(expectedEnrichedRegularClass)
  }

  private def classDefOf(name: String, mods: List[Mod] = Nil) = {
    Defn.Class(
      mods = mods,
      name = Type.Name(name),
      tparams = Nil,
      ctor = PrimaryCtors.Empty,
      templ = Templates.Empty
    )
  }
}
