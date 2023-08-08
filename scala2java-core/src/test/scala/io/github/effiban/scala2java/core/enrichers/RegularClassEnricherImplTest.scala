package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.contexts.matchers.TemplateEnrichmentContextMockitoMatcher.eqTemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities._
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedRegularClassScalatestMatcher.equalEnrichedRegularClass
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaModifiersResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Selfs}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Ctor, Defn, Mod, Name, Self, Template, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class RegularClassEnricherImplTest extends UnitTestSuite {

  private val TheParentJavaScope = JavaScope.Package

  private val TheClassName = t"MyClass"

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


  private val templateEnricher = mock[TemplateEnricher]
  private val javaModifiersResolver = mock[JavaModifiersResolver]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]


  private val regularClassEnricher = new RegularClassEnricherImpl(
    templateEnricher,
    javaModifiersResolver,
    javaTreeTypeResolver,
    javaChildScopeResolver
  )


  test("enrich() when resolves to Java class, and has inits and inheritance keyword") {
    val stats = List(TheDefnVar, TheDefnDef)
    val enrichedStats = List(TheEnrichedDefnVar, TheEnrichedDefnDef)

    val template =
      Template(
        early = List(),
        inits = TheInits,
        self = TheSelf,
        stats = stats
      )

    val theClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = TypeParams,
      ctor = TheCtor,
      templ = template
    )
    val context = StatContext(TheParentJavaScope)

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.Class
    val expectedTemplateContext = TemplateEnrichmentContext(
      javaScope = expectedChildJavaScope,
      maybeClassName = Some(TheClassName)
    )

    val expectedEnrichedTemplate = EnrichedTemplate(
      maybeInheritanceKeyword = Some(TheInheritanceKeyword),
      inits = TheInits,
      self = TheSelf,
      enrichedStats = enrichedStats
    )
    val expectedEnrichedRegularClass = EnrichedRegularClass(
      scalaMods = TheScalaMods,
      javaModifiers = TheJavaModifiers,
      name = TheClassName,
      tparams = TypeParams,
      ctor = TheCtor,
      maybeInheritanceKeyword = Some(TheInheritanceKeyword),
      inits = TheInits,
      self = TheSelf,
      enrichedStats = enrichedStats
    )

    expectResolveJavaTreeType(theClass, TheScalaMods, expectedJavaTreeType)
    doReturn(TheJavaModifiers).when(javaModifiersResolver).resolve(eqExpectedScalaMods(theClass, expectedJavaTreeType))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(theClass, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    doReturn(expectedEnrichedTemplate)
      .when(templateEnricher).enrich(eqTree(template), eqTemplateEnrichmentContext(expectedTemplateContext))

    regularClassEnricher.enrich(theClass, context) should equalEnrichedRegularClass(expectedEnrichedRegularClass)
  }

  test("enrich() when resolves to Java class, and has no inits and no inheritance keyword") {
    val stats = List(TheDefnVar, TheDefnDef)
    val enrichedStats = List(TheEnrichedDefnVar, TheEnrichedDefnDef)

    val template =
      Template(
        early = List(),
        inits = Nil,
        self = TheSelf,
        stats = stats
      )

    val theClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = TypeParams,
      ctor = TheCtor,
      templ = template
    )
    val context = StatContext(TheParentJavaScope)

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.Class
    val expectedTemplateContext = TemplateEnrichmentContext(
      javaScope = expectedChildJavaScope,
      maybeClassName = Some(TheClassName)
    )

    val expectedEnrichedTemplate = EnrichedTemplate(
      self = TheSelf,
      enrichedStats = enrichedStats
    )
    val expectedEnrichedRegularClass = EnrichedRegularClass(
      scalaMods = TheScalaMods,
      javaModifiers = TheJavaModifiers,
      name = TheClassName,
      tparams = TypeParams,
      ctor = TheCtor,
      self = TheSelf,
      enrichedStats = enrichedStats
    )

    expectResolveJavaTreeType(theClass, TheScalaMods, expectedJavaTreeType)
    doReturn(TheJavaModifiers).when(javaModifiersResolver).resolve(eqExpectedScalaMods(theClass, expectedJavaTreeType))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(theClass, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    doReturn(expectedEnrichedTemplate)
      .when(templateEnricher).enrich(eqTree(template), eqTemplateEnrichmentContext(expectedTemplateContext))

    regularClassEnricher.enrich(theClass, context) should equalEnrichedRegularClass(expectedEnrichedRegularClass)
  }

  test("enrich() when resolves to Java enum") {
    val enumConstantsVar = q"final var First, Second = Value"
    val enrichedEnumConstantsVar = EnrichedEnumConstantList(q"final var First, Second = Value")

    val template = Template(
      early = List(),
      inits = Nil,
      self = Selfs.Empty,
      stats = List(enumConstantsVar)
    )
    val theClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = PrimaryCtors.Empty,
      templ = template
    )
    val context = StatContext(TheParentJavaScope)

    val expectedJavaTreeType = JavaTreeType.Enum
    val expectedChildJavaScope = JavaScope.Enum

    val expectedTemplateContext = TemplateEnrichmentContext(
      javaScope = expectedChildJavaScope,
      maybeClassName = Some(TheClassName)
    )

    val expectedEnrichedTemplate = EnrichedTemplate(
      enrichedStats = List(enrichedEnumConstantsVar)
    )
    val expectedEnrichedRegularClass = EnrichedRegularClass(
      scalaMods = TheScalaMods,
      javaModifiers = TheJavaModifiers,
      javaTypeKeyword = JavaKeyword.Enum,
      name = TheClassName,
      tparams = Nil,
      ctor = PrimaryCtors.Empty,
      self = Selfs.Empty,
      enrichedStats = List(enrichedEnumConstantsVar)
    )

    expectResolveJavaTreeType(theClass, TheScalaMods, expectedJavaTreeType)
    doReturn(TheJavaModifiers).when(javaModifiersResolver).resolve(eqExpectedScalaMods(theClass, expectedJavaTreeType))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(theClass, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    doReturn(expectedEnrichedTemplate)
      .when(templateEnricher).enrich(eqTree(template), eqTemplateEnrichmentContext(expectedTemplateContext))

    regularClassEnricher.enrich(theClass, context) should equalEnrichedRegularClass(expectedEnrichedRegularClass)
  }

  private def expectResolveJavaTreeType(defnClass: Defn.Class, scalaMods: List[Mod], expectedJavaTreeType: JavaTreeType): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(defnClass, scalaMods)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(expectedJavaTreeType)
  }

  private def eqExpectedScalaMods(defnClass: Defn.Class, javaTreeType: JavaTreeType) = {
    val expectedModifiersContext = ModifiersContext(defnClass, javaTreeType, TheParentJavaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
