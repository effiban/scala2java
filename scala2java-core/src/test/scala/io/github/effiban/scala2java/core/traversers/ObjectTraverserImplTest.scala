package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.core.traversers.results.matchers.ObjectTraversalResultScalatestMatcher.equalObjectTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Defn, Mod, Name, Self, Template, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ObjectTraverserImplTest extends UnitTestSuite {

  private val TheParentJavaScope = JavaScope.Package

  private val TheScalaMods: List[Mod] = List(mod"@MyAnnotation1", mod"@MyAnnotation2")
  private val TheTraversedScalaMods: List[Mod] = List(mod"@MyTraversedAnnotation1", mod"@MyTraversedAnnotation2")

  private val TheObjectName = q"MyObject"

  private val Init1 = init"Parent1()"
  private val Init2 = init"Parent2()"
  private val TheInits = List(Init1, Init2)

  private val TraversedInit1 = init"TraversedParent1()"
  private val TraversedInit2 = init"TraversedParent2()"
  private val TheTraversedInits = List(TraversedInit1, TraversedInit2)

  private val TheSelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(t"SelfType"))
  private val TheTraversedSelf = Self(name = Name.Indeterminate("TraversedSelfName"), decltpe = Some(t"SelfType"))

  private val TheDefnVar = q"var y = 4"
  private val TheTraversedDefnVar = q"var yy = 44"
  private val TheDefnVarTraversalResult = DefnVarTraversalResult(TheTraversedDefnVar)

  private val TheDefnDef = q"def myMethod(param: Int): Int = doSomething(param)"
  private val TheTraversedDefnDef = q"def myTraversedMethod(param2: Int): Int = doSomething(param2)"
  private val TheDefnDefTraversalResult = DefnDefTraversalResult(TheTraversedDefnDef)

  private val TheStats = List(TheDefnVar, TheDefnDef)
  private val TheTraversedStatResults = List(TheDefnVarTraversalResult, TheDefnDefTraversalResult)

  private val statModListTraverser = mock[StatModListTraverser]
  private val templateTraverser = mock[TemplateTraverser]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]

  private val objectTraverser = new ObjectTraverserImpl(
    statModListTraverser,
    templateTraverser,
    javaTreeTypeResolver,
    javaChildScopeResolver
  )


  test("traverse() when resolves to a utility class") {
    val template = Template(
      early = List(),
      inits = Nil,
      self = TheSelf,
      stats = TheStats
    )

    val objectDef = Defn.Object(
      mods = TheScalaMods,
      name = TheObjectName,
      templ = template
    )

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.UtilityClass
    val expectedTemplateContext = TemplateContext(javaScope = expectedChildJavaScope)
    val expectedJavaModifiers = List(JavaModifier.Public)
    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = TheTraversedScalaMods, javaModifiers = expectedJavaModifiers)
    val expectedTemplateTraversalResult = TemplateTraversalResult(
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )
    val expectedObjectTraversalResult = ObjectTraversalResult(
      scalaMods = TheTraversedScalaMods,
      javaModifiers = expectedJavaModifiers,
      javaTypeKeyword = JavaKeyword.Class,
      name = TheObjectName,
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )

    expectResolveJavaTreeType(objectDef, TheScalaMods, expectedJavaTreeType)
    doReturn(expectedModListTraversalResult)
      .when(statModListTraverser).traverse(eqExpectedScalaMods(objectDef, expectedJavaTreeType, TheParentJavaScope))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    doReturn(expectedTemplateTraversalResult)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    objectTraverser.traverse(objectDef, StatContext(TheParentJavaScope)) should equalObjectTraversalResult(expectedObjectTraversalResult)
  }

  test("traverse() when resolves to a regular class with inheritance") {
    val template = Template(
      early = List(),
      inits = TheInits,
      self = TheSelf,
      stats = TheStats
    )

    val objectDef = Defn.Object(
      mods = TheScalaMods,
      name = TheObjectName,
      templ = template
    )

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.Class
    val expectedInheritanceKeyword = JavaKeyword.Implements
    val expectedTemplateContext = TemplateContext(javaScope = expectedChildJavaScope)
    val expectedJavaModifiers = List(JavaModifier.Public)
    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = TheTraversedScalaMods, javaModifiers = expectedJavaModifiers)
    val expectedTemplateTraversalResult = TemplateTraversalResult(
      maybeInheritanceKeyword = Some(expectedInheritanceKeyword),
      inits = TheTraversedInits,
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )
    val expectedObjectTraversalResult = ObjectTraversalResult(
      scalaMods = TheTraversedScalaMods,
      javaModifiers = expectedJavaModifiers,
      javaTypeKeyword = JavaKeyword.Class,
      name = TheObjectName,
      maybeInheritanceKeyword = Some(expectedInheritanceKeyword),
      inits = TheTraversedInits,
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )

    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)

    expectResolveJavaTreeType(objectDef, TheScalaMods, expectedJavaTreeType)
    doReturn(expectedModListTraversalResult)
      .when(statModListTraverser).traverse(eqExpectedScalaMods(objectDef, expectedJavaTreeType, TheParentJavaScope))
    doReturn(expectedTemplateTraversalResult)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    objectTraverser.traverse(objectDef, StatContext(TheParentJavaScope)) should equalObjectTraversalResult(expectedObjectTraversalResult)
  }

  test("traverse() when resolves to an enum") {
    val template = Template(
      early = List(),
      inits = Nil,
      self = TheSelf,
      stats = TheStats
    )

    val objectDef = Defn.Object(
      mods = TheScalaMods,
      name = TheObjectName,
      templ = template
    )

    val expectedJavaTreeType = JavaTreeType.Enum
    val expectedChildJavaScope = JavaScope.Enum
    val expectedTemplateContext = TemplateContext(javaScope = expectedChildJavaScope)
    val expectedJavaModifiers = List(JavaModifier.Public)
    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = TheTraversedScalaMods, javaModifiers = expectedJavaModifiers)
    val expectedTemplateTraversalResult = TemplateTraversalResult(
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )
    val expectedObjectTraversalResult = ObjectTraversalResult(
      scalaMods = TheTraversedScalaMods,
      javaModifiers = expectedJavaModifiers,
      javaTypeKeyword = JavaKeyword.Enum,
      name = TheObjectName,
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )

    expectResolveJavaTreeType(objectDef, TheScalaMods, expectedJavaTreeType)
    doReturn(expectedModListTraversalResult)
      .when(statModListTraverser).traverse(eqExpectedScalaMods(objectDef, expectedJavaTreeType, TheParentJavaScope))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    doReturn(expectedTemplateTraversalResult)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    objectTraverser.traverse(objectDef, StatContext(TheParentJavaScope)) should equalObjectTraversalResult(expectedObjectTraversalResult)
  }

  private def expectResolveJavaTreeType(obj: Defn.Object, modifiers: List[Mod], expectedJavaTreeType: JavaTreeType): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(obj, modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(expectedJavaTreeType)
  }

  private def eqExpectedScalaMods(obj: Defn.Object, javaTreeType: JavaTreeType, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(obj, javaTreeType, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
