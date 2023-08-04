package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.contexts.matchers.TemplateEnrichmentContextMockitoMatcher.eqTemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities._
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedObjectScalatestMatcher.equalEnrichedObject
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaModifiersResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Defn, Mod, Name, Self, Template, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ObjectEnricherImplTest extends UnitTestSuite {

  private val TheParentJavaScope = JavaScope.Package

  private val TheScalaMods: List[Mod] = List(mod"@MyAnnotation1", mod"@MyAnnotation2")

  private val TheJavaModifiers = List(JavaModifier.Public)

  private val TheObjectName = q"MyObject"

  private val Init1 = init"Parent1()"
  private val Init2 = init"Parent2()"
  private val TheInits = List(Init1, Init2)

  private val TheSelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(t"SelfType"))

  private val TheDefnVar = q"var y = 4"
  private val TheEnrichedDefnVar = EnrichedDefnVar(TheDefnVar)

  private val TheDefnDef = q"def myMethod(param: Int): Int = doSomething(param)"
  private val TheEnrichedDefnDef = EnrichedDefnDef(TheDefnDef)

  private val templateEnricher = mock[TemplateEnricher]
  private val javaModifiersResolver = mock[JavaModifiersResolver]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]

  private val objectEnricher = new ObjectEnricherImpl(
    templateEnricher,
    javaModifiersResolver,
    javaTreeTypeResolver,
    javaChildScopeResolver
  )


  test("enrich() when resolves to a Java utility class") {
    val stats = List(TheDefnVar, TheDefnDef)
    val enrichedStats = List(TheEnrichedDefnVar, TheEnrichedDefnDef)

    val template = Template(
      early = List(),
      inits = Nil,
      self = TheSelf,
      stats = stats
    )

    val objectDef = Defn.Object(
      mods = TheScalaMods,
      name = TheObjectName,
      templ = template
    )

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.UtilityClass
    val expectedTemplateContext = TemplateEnrichmentContext(javaScope = expectedChildJavaScope)
    val expectedEnrichedTemplate = EnrichedTemplate(
      self = TheSelf,
      enrichedStats = enrichedStats
    )
    val expectedEnrichedObject = EnrichedObject(
      scalaMods = TheScalaMods,
      javaModifiers = TheJavaModifiers,
      javaTypeKeyword = JavaKeyword.Class,
      name = TheObjectName,
      self = TheSelf,
      enrichedStats = enrichedStats
    )

    expectResolveJavaTreeType(objectDef, TheScalaMods, expectedJavaTreeType)
    doReturn(TheJavaModifiers).when(javaModifiersResolver).resolve(eqExpectedScalaMods(objectDef, expectedJavaTreeType))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    doReturn(expectedEnrichedTemplate)
      .when(templateEnricher).enrich(eqTree(template), eqTemplateEnrichmentContext(expectedTemplateContext))

    objectEnricher.enrich(objectDef, StatContext(TheParentJavaScope)) should equalEnrichedObject(expectedEnrichedObject)
  }

  test("enrich() when resolves to a regular Java class with inheritance") {
    val stats = List(TheDefnVar, TheDefnDef)
    val enrichedStats = List(TheEnrichedDefnVar, TheEnrichedDefnDef)

    val template = Template(
      early = List(),
      inits = TheInits,
      self = TheSelf,
      stats = stats
    )

    val objectDef = Defn.Object(
      mods = TheScalaMods,
      name = TheObjectName,
      templ = template
    )

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.Class
    val expectedInheritanceKeyword = JavaKeyword.Implements
    val expectedTemplateContext = TemplateEnrichmentContext(javaScope = expectedChildJavaScope)
    val expectedEnrichedTemplate = EnrichedTemplate(
      maybeInheritanceKeyword = Some(expectedInheritanceKeyword),
      inits = TheInits,
      self = TheSelf,
      enrichedStats = enrichedStats
    )
    val expectedEnrichedObject = EnrichedObject(
      scalaMods = TheScalaMods,
      javaModifiers = TheJavaModifiers,
      javaTypeKeyword = JavaKeyword.Class,
      name = TheObjectName,
      maybeInheritanceKeyword = Some(expectedInheritanceKeyword),
      inits = TheInits,
      self = TheSelf,
      enrichedStats = enrichedStats
    )

    expectResolveJavaTreeType(objectDef, TheScalaMods, expectedJavaTreeType)
    doReturn(TheJavaModifiers).when(javaModifiersResolver).resolve(eqExpectedScalaMods(objectDef, expectedJavaTreeType))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    doReturn(expectedEnrichedTemplate)
      .when(templateEnricher).enrich(eqTree(template), eqTemplateEnrichmentContext(expectedTemplateContext))

    objectEnricher.enrich(objectDef, StatContext(TheParentJavaScope)) should equalEnrichedObject(expectedEnrichedObject)
  }

  test("enrich() when resolves to a Java enum") {
    val enumConstantsVar = q"final var First, Second = Value"
    val enrichedEnumConstantsVar = EnrichedEnumConstantList(q"final var First, Second = Value")

    val template = Template(
      early = List(),
      inits = Nil,
      self = TheSelf,
      stats = List(enumConstantsVar)
    )

    val objectDef = Defn.Object(
      mods = TheScalaMods,
      name = TheObjectName,
      templ = template
    )

    val expectedJavaTreeType = JavaTreeType.Enum
    val expectedChildJavaScope = JavaScope.Enum
    val expectedTemplateContext = TemplateEnrichmentContext(javaScope = expectedChildJavaScope)
    val expectedEnrichedTemplate = EnrichedTemplate(
      self = TheSelf,
      enrichedStats = List(enrichedEnumConstantsVar)
    )
    val expectedEnrichedObject = EnrichedObject(
      scalaMods = TheScalaMods,
      javaModifiers = TheJavaModifiers,
      javaTypeKeyword = JavaKeyword.Enum,
      name = TheObjectName,
      self = TheSelf,
      enrichedStats = List(enrichedEnumConstantsVar)
    )

    expectResolveJavaTreeType(objectDef, TheScalaMods, expectedJavaTreeType)
    doReturn(TheJavaModifiers).when(javaModifiersResolver).resolve(eqExpectedScalaMods(objectDef, expectedJavaTreeType))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    doReturn(expectedEnrichedTemplate)
      .when(templateEnricher).enrich(eqTree(template), eqTemplateEnrichmentContext(expectedTemplateContext))

    objectEnricher.enrich(objectDef, StatContext(TheParentJavaScope)) should equalEnrichedObject(expectedEnrichedObject)
  }

  private def expectResolveJavaTreeType(obj: Defn.Object, modifiers: List[Mod], expectedJavaTreeType: JavaTreeType): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(obj, modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(expectedJavaTreeType)
  }

  private def eqExpectedScalaMods(obj: Defn.Object, javaTreeType: JavaTreeType) = {
    val expectedModifiersContext = ModifiersContext(obj, javaTreeType, TheParentJavaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
