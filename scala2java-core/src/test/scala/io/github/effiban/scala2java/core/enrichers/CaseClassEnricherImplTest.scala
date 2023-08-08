package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.contexts.matchers.TemplateEnrichmentContextMockitoMatcher.eqTemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedCaseClassScalatestMatcher.equalEnrichedCaseClass
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedCaseClass, EnrichedDefnDef, EnrichedDefnVar, EnrichedTemplate}
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaModifiersResolver}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Ctor, Defn, Mod, Name, Self, Template, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class CaseClassEnricherImplTest extends UnitTestSuite {

  private val TheCaseClassName = t"MyCaseClass"

  private val TheScalaMods: List[Mod] = List(mod"@MyAnnotation1", mod"@MyAnnotation2")

  private val TheJavaModifiers = List(JavaModifier.Public)

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val CtorArg1 = param"arg1: Int"
  private val CtorArg2 = param"arg2: Int"
  private val CtorArg3 = param"arg3: Int"
  private val CtorArg4 = param"arg4: Int"

  private val CtorArgList1 = List(CtorArg1, CtorArg2)
  private val CtorArgList2 = List(CtorArg3, CtorArg4)

  private val TheCtor = Ctor.Primary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(CtorArgList1, CtorArgList2)
  )

  private val Init1 = init"Parent1()"
  private val Init2 = init"Parent2()"
  private val TheInits = List(Init1, Init2)

  private val TheInheritanceKeyword = JavaKeyword.Implements

  private val TheSelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(t"SelfType"))

  private val TheDefnVar = q"var y = 4"
  private val TheEnrichedDefnVar = EnrichedDefnVar(TheDefnVar)

  private val TheDefnDef = q"def myMethod(param: Int): Int = doSomething(param)"
  private val TheEnrichedDefnDef = EnrichedDefnDef(TheDefnDef)

  private val TheStats = List(TheDefnVar, TheDefnDef)
  private val TheEnrichedStats = List(TheEnrichedDefnVar, TheEnrichedDefnDef)

  private val templateEnricher = mock[TemplateEnricher]
  private val javaModifiersResolver = mock[JavaModifiersResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]


  private val caseClassEnricher = new CaseClassEnricherImpl(
    templateEnricher,
    javaModifiersResolver,
    javaChildScopeResolver
  )


  test("enrich() when has inits and inheritance keyword") {
    val template =
      Template(
        early = List(),
        inits = TheInits,
        self = TheSelf,
        stats = TheStats
      )

    val caseClass = Defn.Class(
      mods = TheScalaMods,
      name = TheCaseClassName,
      tparams = TypeParams,
      ctor = TheCtor,
      templ = template
    )
    val context = StatContext(javaScope = JavaScope.Package)

    val expectedJavaModifiers = List(JavaModifier.Public)
    val expectedTemplateContext = TemplateEnrichmentContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(TheCaseClassName)
    )

    val expectedEnrichedTemplate = EnrichedTemplate(
      maybeInheritanceKeyword = Some(TheInheritanceKeyword),
      inits = TheInits,
      self = TheSelf,
      enrichedStats = TheEnrichedStats
    )
    val expectedEnrichedCaseClass = EnrichedCaseClass(
      scalaMods = TheScalaMods,
      javaModifiers = expectedJavaModifiers,
      name = TheCaseClassName,
      tparams = TypeParams,
      ctor = TheCtor,
      maybeInheritanceKeyword = Some(TheInheritanceKeyword),
      inits = TheInits,
      self = TheSelf,
      enrichedStats = TheEnrichedStats
    )

    doReturn(TheJavaModifiers).when(javaModifiersResolver).resolve(eqExpectedScalaMods(caseClass))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(caseClass, JavaTreeType.Record))))
      .thenReturn(JavaScope.Class)
    doReturn(expectedEnrichedTemplate)
      .when(templateEnricher).enrich(eqTree(template), eqTemplateEnrichmentContext(expectedTemplateContext))

    caseClassEnricher.enrich(caseClass, context) should equalEnrichedCaseClass(expectedEnrichedCaseClass)
  }

  test("enrich() when has no inits and no inheritance keyword") {
    val template =
      Template(
        early = List(),
        inits = Nil,
        self = TheSelf,
        stats = TheStats
      )

    val caseClass = Defn.Class(
      mods = TheScalaMods,
      name = TheCaseClassName,
      tparams = TypeParams,
      ctor = TheCtor,
      templ = template
    )
    val context = StatContext(javaScope = JavaScope.Package)

    val expectedJavaModifiers = List(JavaModifier.Public)
    val expectedTemplateContext = TemplateEnrichmentContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(TheCaseClassName)
    )

    val expectedEnrichedTemplate = EnrichedTemplate(
      self = TheSelf,
      enrichedStats = TheEnrichedStats
    )
    val expectedEnrichedCaseClass = EnrichedCaseClass(
      scalaMods = TheScalaMods,
      javaModifiers = expectedJavaModifiers,
      name = TheCaseClassName,
      tparams = TypeParams,
      ctor = TheCtor,
      self = TheSelf,
      enrichedStats = TheEnrichedStats
    )

    doReturn(TheJavaModifiers).when(javaModifiersResolver).resolve(eqExpectedScalaMods(caseClass))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(caseClass, JavaTreeType.Record))))
      .thenReturn(JavaScope.Class)
    doReturn(expectedEnrichedTemplate)
      .when(templateEnricher).enrich(eqTree(template), eqTemplateEnrichmentContext(expectedTemplateContext))

    caseClassEnricher.enrich(caseClass, context) should equalEnrichedCaseClass(expectedEnrichedCaseClass)
  }

  private def eqExpectedScalaMods(caseClass: Defn.Class) = {
    val expectedModifiersContext = ModifiersContext(caseClass, JavaTreeType.Record, JavaScope.Package)
    eqModifiersContext(expectedModifiersContext)
  }
}
