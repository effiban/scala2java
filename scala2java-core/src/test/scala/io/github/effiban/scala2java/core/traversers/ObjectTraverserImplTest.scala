package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
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

  private val TheDefnDef = q"def myMethod(param: Int): Int = doSomething(param)"
  private val TheTraversedDefnDef = q"def myTraversedMethod(param2: Int): Int = doSomething(param2)"

  private val TheStats = List(TheDefnVar, TheDefnDef)
  private val TheTraversedStats = List(TheTraversedDefnVar, TheTraversedDefnDef)

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


  test("traverse() when resolves to a Java utility class") {
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
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = Nil,
      self = TheTraversedSelf,
      stats = TheTraversedStats
    )
    val expectedTraversedObject = Defn.Object(
      mods = TheTraversedScalaMods,
      name = TheObjectName,
      templ = expectedTraversedTemplate
    )

    expectResolveJavaTreeType(objectDef, TheScalaMods, expectedJavaTreeType)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    objectTraverser.traverse(objectDef, StatContext(TheParentJavaScope)).structure shouldBe expectedTraversedObject.structure
  }

  test("traverse() when resolves to a regular Java class with inheritance") {
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
    val expectedTemplateContext = TemplateContext(javaScope = expectedChildJavaScope)
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = TheTraversedInits,
      self = TheTraversedSelf,
      stats = TheTraversedStats
    )
    val expectedTraversedObject = Defn.Object(
      mods = TheTraversedScalaMods,
      name = TheObjectName,
      templ = expectedTraversedTemplate
    )

    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    expectResolveJavaTreeType(objectDef, TheScalaMods, expectedJavaTreeType)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    objectTraverser.traverse(objectDef, StatContext(TheParentJavaScope)).structure shouldBe expectedTraversedObject.structure
  }

  test("traverse() when resolves to a Java enum") {
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
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = Nil,
      self = TheTraversedSelf,
      stats = TheTraversedStats
    )
    val expectedTraversedObject = Defn.Object(
      mods = TheTraversedScalaMods,
      name = TheObjectName,
      templ = expectedTraversedTemplate
    )

    expectResolveJavaTreeType(objectDef, TheScalaMods, expectedJavaTreeType)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(objectDef, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)
    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    objectTraverser.traverse(objectDef, StatContext(TheParentJavaScope)).structure shouldBe expectedTraversedObject.structure
  }

  private def expectResolveJavaTreeType(obj: Defn.Object, modifiers: List[Mod], expectedJavaTreeType: JavaTreeType): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(obj, modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(expectedJavaTreeType)
  }
}
