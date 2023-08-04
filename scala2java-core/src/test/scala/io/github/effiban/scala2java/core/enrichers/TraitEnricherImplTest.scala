package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.contexts.matchers.TemplateEnrichmentContextMockitoMatcher.eqTemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedTraitScalatestMatcher.equalEnrichedTrait
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDefnDef, EnrichedDefnVar, EnrichedTemplate, EnrichedTrait}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.PrimaryCtors
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Defn.Trait
import scala.meta.{Defn, Mod, Name, Self, Template, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TraitEnricherImplTest extends UnitTestSuite {

  private val TraitName = t"MyTrait"

  private val TheScalaMods: List[Mod] = List(mod"@MyAnnotation1", mod"@MyAnnotation2")

  private val TheJavaModifiers = List(JavaModifier.Public)

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val Init1 = init"Parent1()"
  private val Init2 = init"Parent2()"
  private val TheInits = List(Init1, Init2)

  private val TheSelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(t"SelfType"))

  private val TheDefnVar = q"var y = 4"
  private val TheEnrichedDefnVar = EnrichedDefnVar(TheDefnVar)

  private val TheDefnDef = q"def myMethod(param: Int): Int = doSomething(param)"
  private val TheDefnDefTraversalResult = EnrichedDefnDef(TheDefnDef)

  private val TheStats = List(TheDefnVar, TheDefnDef)
  private val TheEnrichedStats = List(TheEnrichedDefnVar, TheDefnDefTraversalResult)

  private val TheTemplate =
    Template(
      early = List(),
      inits = TheInits,
      self = TheSelf,
      stats = TheStats
    )

  private val templateEnricher = mock[TemplateEnricher]
  private val javaModifiersResolver = mock[JavaModifiersResolver]


  private val traitEnricher = new TraitEnricherImpl(templateEnricher, javaModifiersResolver)


  test("enrich()") {
    val `trait` = Defn.Trait(
      mods = TheScalaMods,
      name = TraitName,
      tparams = TypeParams,
      ctor = PrimaryCtors.Empty,
      templ = TheTemplate
    )

    val expectedJavaModifiers = List(JavaModifier.Public)
    val expectedEnrichedTemplate = EnrichedTemplate(
      inits = TheInits,
      self = TheSelf,
      enrichedStats = TheEnrichedStats
    )
    val expectedEnrichedTrait = EnrichedTrait(
      scalaMods = TheScalaMods,
      javaModifiers = expectedJavaModifiers,
      name = TraitName,
      tparams = TypeParams,
      inits = TheInits,
      self = TheSelf,
      enrichedStats = TheEnrichedStats
    )

    doReturn(TheJavaModifiers).when(javaModifiersResolver).resolve(eqExpectedScalaMods(`trait`))
    doReturn(expectedEnrichedTemplate)
      .when(templateEnricher).enrich(
        eqTree(TheTemplate),
        eqTemplateEnrichmentContext(TemplateEnrichmentContext(javaScope = JavaScope.Interface)))


    val context = StatContext(javaScope = JavaScope.Package)
    traitEnricher.enrich(`trait`, context) should equalEnrichedTrait(expectedEnrichedTrait)
  }

  private def eqExpectedScalaMods(`trait`: Trait) = {
    val expectedModifiersContext = ModifiersContext(`trait`, JavaTreeType.Interface, JavaScope.Package)
    eqModifiersContext(expectedModifiersContext)
  }
}
