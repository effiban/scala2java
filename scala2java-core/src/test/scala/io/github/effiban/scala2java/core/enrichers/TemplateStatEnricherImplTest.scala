package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.classifiers.DefnVarClassifier
import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.matchers.CtorSecondaryEnrichmentContextMockitoMatcher.eqCtorSecondaryEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.contexts.{CtorSecondaryEnrichmentContext, TemplateBodyEnrichmentContext}
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedStatScalatestMatcher.equalEnrichedStat
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedCtorSecondary, EnrichedDefnDef, EnrichedDefnVar, EnrichedEnumConstantList}
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Init, Name, Term, Type, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType}

class TemplateStatEnricherImplTest extends UnitTestSuite {

  private val ClassName = t"MyClass"

  private val TheAnnot = mod"@MyAnnotation"
  private val TheScalaMods = List(TheAnnot)

  private val CtorSecondaryContext = CtorSecondaryEnrichmentContext(JavaScope.Class, ClassName)
  private val TemplateBodyContextWithClassName = TemplateBodyEnrichmentContext(JavaScope.Class, Some(ClassName))

  private val SecondaryCtorArgs = List(param"arg3: Int", param"arg4: Int")

  private val SecondaryCtor = Ctor.Secondary(
    mods = TheScalaMods,
    name = Name.Anonymous(),
    paramss = List(SecondaryCtorArgs),
    init = Init(tpe = Type.Singleton(Term.This(Name.Anonymous())), name = Name.Anonymous(), argss = List(Nil)),
    stats = List(q"foo(1)", q"bar(2)")
  )

  private val TheDefnVar = q"var y = 4"

  private val TheDefnDef = q"def myMethod(param: Int) = foo(param)"

  private val ctorSecondaryEnricher = mock[CtorSecondaryEnricher]
  private val defaultStatEnricher = mock[DefaultStatEnricher]
  private val defnValClassifier = mock[DefnVarClassifier]

  private val templateStatEnricher = new TemplateStatEnricherImpl(
    ctorSecondaryEnricher,
    defaultStatEnricher,
    defnValClassifier,
  )

  test("enrich() for secondary ctor. when class name provided") {
    val ctorJavaModifiers = List(JavaModifier.Public)
    val expectedEnrichedCtorSecondary = EnrichedCtorSecondary(
      stat = SecondaryCtor,
      className = ClassName,
      javaModifiers = ctorJavaModifiers
    )
    doReturn(expectedEnrichedCtorSecondary)
      .when(ctorSecondaryEnricher).enrich(eqTree(SecondaryCtor), eqCtorSecondaryEnrichmentContext(CtorSecondaryContext))

    val actualEnrichedCtorSecondary = templateStatEnricher.enrich(stat = SecondaryCtor, context = TemplateBodyContextWithClassName)
    actualEnrichedCtorSecondary should equalEnrichedStat(expectedEnrichedCtorSecondary)
  }

  test("enrich() for secondary ctor. without class name should throw exception") {
    intercept[IllegalStateException] {
      templateStatEnricher.enrich(stat = SecondaryCtor, context = TemplateBodyEnrichmentContext(javaScope = JavaScope.Class))
    }
  }

  test("enrich() for Defn.Var which is not an enum constant list") {
    val expectedEnrichedDefnVar = EnrichedDefnVar(TheDefnVar)

    when(defnValClassifier.isEnumConstantList(eqTree(TheDefnVar), eqTo(JavaScope.Class))).thenReturn(false)
    doReturn(expectedEnrichedDefnVar)
      .when(defaultStatEnricher).enrich(eqTree(TheDefnVar), eqTo(StatContext(JavaScope.Class)))

    val actualEnrichedDefnVar = templateStatEnricher.enrich(
      stat = TheDefnVar,
      context = TemplateBodyEnrichmentContext(javaScope = JavaScope.Class)
    )
    actualEnrichedDefnVar should equalEnrichedStat(expectedEnrichedDefnVar)
  }

  test("enrich() for Defn.Var which is an enum constant list") {
    val expectedEnrichedDefnVar = EnrichedEnumConstantList(TheDefnVar)

    when(defnValClassifier.isEnumConstantList(eqTree(TheDefnVar), eqTo(JavaScope.Class))).thenReturn(true)

    val actualEnrichedDefnVar = templateStatEnricher.enrich(
      stat = TheDefnVar,
      context = TemplateBodyEnrichmentContext(javaScope = JavaScope.Class)
    )
    actualEnrichedDefnVar should equalEnrichedStat(expectedEnrichedDefnVar)
  }

  test("enrich() for a Defn.Def") {
    val expectedEnrichedDefnDef = EnrichedDefnDef(TheDefnDef)

    doReturn(expectedEnrichedDefnDef)
      .when(defaultStatEnricher).enrich(eqTree(TheDefnDef), eqTo(StatContext(JavaScope.Class)))

    val actualResult = templateStatEnricher.enrich(
      stat = TheDefnDef,
      context = TemplateBodyEnrichmentContext(javaScope = JavaScope.Class)
    )
    actualResult should equalEnrichedStat(expectedEnrichedDefnDef)
  }
}
