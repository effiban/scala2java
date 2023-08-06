package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.matchers.JavaChildScopeContextMatcher.eqJavaChildScopeContext
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Selfs}
import io.github.effiban.scala2java.core.transformers.ParamToDeclVarTransformer
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Ctor, Defn, Mod, Name, Self, Template, Term, Type, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class RegularClassTraverserImplTest extends UnitTestSuite {

  private val TheParentJavaScope = JavaScope.Package

  private val TheClassName = t"MyClass"

  private val TheScalaMods = List(mod"@MyAnnotation1", mod"@MyAnnotation2")
  private val TheTraversedScalaMods = List(mod"@MyTraversedAnnotation1", mod"@MyTraversedAnnotation2")

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TheTypeParams = List(TypeParam1, TypeParam2)

  private val TraversedTypeParam1 = tparam"T11"
  private val TraversedTypeParam2 = tparam"T22"
  private val TheTraversedTypeParams = List(TraversedTypeParam1, TraversedTypeParam2)

  private val CtorArg1 = param"arg1: Int"
  private val CtorArg2 = param"arg2: Int"
  private val CtorArg3 = param"arg3: Int"
  private val CtorArg4 = param"arg4: Int"

  private val CtorArgList1 = List(CtorArg1, CtorArg2)
  private val CtorArgList2 = List(CtorArg3, CtorArg4)

  private val Init1 = init"Parent1()"
  private val Init2 = init"Parent2()"
  private val TheInits = List(Init1, Init2)

  private val TraversedInit1 = init"TraversedParent1()"
  private val TraversedInit2 = init"TraversedParent2()"
  private val TheTraversedInits = List(TraversedInit1, TraversedInit2)

  private val TheSelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(t"SelfType"))
  private val TheTraversedSelf = Self(name = Name.Indeterminate("TraversedSelfName"), decltpe = Some(t"SelfType"))

  private val SyntheticDeclVar1 = q"private final var arg1: Int"
  private val SyntheticDeclVar2 = q"private final var arg2: Int"
  private val SyntheticDeclVar3 = q"private final var arg3: Int"
  private val SyntheticDeclVar4 = q"private final var arg4: Int"

  private val DefnDef1 = q"def myMethod1(param: Int): Int = doSomething1(param)"
  private val DefnDef2 = q"def myMethod2(param: Int): Int = doSomething2(param)"

  private val TraversedDefnDef1 = q"def myTraversedMethod1(param: Int): Int = doSomething11(param)"
  private val TraversedDefnDef2 = q"def myTraversedMethod2(param: Int): Int = doSomething22(param)"

  private val TheStats = List(DefnDef1, DefnDef2)
  private val TheTraversedStats = List(TraversedDefnDef1, TraversedDefnDef2)


  private val statModListTraverser = mock[StatModListTraverser]
  private val typeParamTraverser = mock[TypeParamTraverser]
  private val templateTraverser = mock[TemplateTraverser]
  private val paramToDeclVarTransformer = mock[ParamToDeclVarTransformer]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]
  private val javaChildScopeResolver = mock[JavaChildScopeResolver]

  private val classTraverser = new RegularClassTraverserImpl(
    statModListTraverser,
    typeParamTraverser,
    templateTraverser,
    paramToDeclVarTransformer,
    javaTreeTypeResolver,
    javaChildScopeResolver
  )

  test("traverse() when resolves to Java class and has one list of ctor args - should create synthetic vars") {
    val ctorPrimary = ctorPrimaryOf(List(CtorArgList1))
    val initialTemplate = Template(
      early = List(),
      inits = Nil,
      self = TheSelf,
      stats = Nil
    )
    val regularClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = initialTemplate
    )

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.Class
    val expectedTemplateContext = TemplateContext(
      javaScope = expectedChildJavaScope,
      maybeClassName = Some(TheClassName),
      maybePrimaryCtor = Some(ctorPrimary)
    )
    val expectedSyntheticDeclVars = List(SyntheticDeclVar1, SyntheticDeclVar2)
    val expectedAdjustedTemplate = initialTemplate.copy(stats = expectedSyntheticDeclVars)
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = Nil,
      self = TheTraversedSelf,
      stats = List(SyntheticDeclVar1, SyntheticDeclVar2)
    )
    val expectedTraversedRegularClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = expectedTraversedTemplate
    )

    expectResolveJavaTreeType(regularClass, TheScalaMods, expectedJavaTreeType)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(regularClass, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)

    when(paramToDeclVarTransformer.transform(any[Term.Param])).thenAnswer( (ctorArg: Term.Param) => ctorArg match {
      case arg1 if arg1.structure == CtorArg1.structure => SyntheticDeclVar1
      case arg2 if arg2.structure == CtorArg2.structure => SyntheticDeclVar2
    })

    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(expectedAdjustedTemplate), eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(regularClass, ClassOrTraitContext(TheParentJavaScope)).structure shouldBe expectedTraversedRegularClass.structure
  }

  test("traverse() when resolves to Java class and has type params") {
    val ctorPrimary = PrimaryCtors.Empty
    val template = Template(
      early = List(),
      inits = Nil,
      self = TheSelf,
      stats = Nil
    )
    val regularClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = TheTypeParams,
      ctor = ctorPrimary,
      templ = template
    )

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.Class
    val expectedTemplateContext = TemplateContext(
      javaScope = expectedChildJavaScope,
      maybeClassName = Some(TheClassName),
      maybePrimaryCtor = Some(ctorPrimary)
    )
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = Nil,
      self = TheTraversedSelf,
      stats = Nil,
    )
    val expectedTraversedRegularClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = TheTraversedTypeParams,
      ctor = ctorPrimary,
      templ = expectedTraversedTemplate
    )

    expectResolveJavaTreeType(regularClass, TheScalaMods, expectedJavaTreeType)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doAnswer((tparam: Type.Param) => tparam match {
      case aTypeParam if aTypeParam.structure == TypeParam1.structure => TraversedTypeParam1
      case aTypeParam if aTypeParam.structure == TypeParam2.structure => TraversedTypeParam2
      case aTypeParam => aTypeParam
    }).when(typeParamTraverser).traverse(any[Type.Param])
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(regularClass, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)

    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(regularClass, ClassOrTraitContext(TheParentJavaScope)).structure shouldBe expectedTraversedRegularClass.structure
  }

  test("traverse() when resolves to Java class with inits") {
    val ctorPrimary = PrimaryCtors.Empty
    val template = Template(
      early = List(),
      inits = TheInits,
      self = TheSelf,
      stats = Nil
    )
    val regularClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = template
    )

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.Class
    val expectedTemplateContext = TemplateContext(
      javaScope = expectedChildJavaScope,
      maybeClassName = Some(TheClassName),
      maybePrimaryCtor = Some(ctorPrimary)
    )
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = TheTraversedInits,
      self = TheTraversedSelf,
      stats = Nil
    )
    val expectedTraversedRegularClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = expectedTraversedTemplate
    )

    expectResolveJavaTreeType(regularClass, TheScalaMods, expectedJavaTreeType)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(regularClass, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)

    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(regularClass, ClassOrTraitContext(TheParentJavaScope)).structure shouldBe expectedTraversedRegularClass.structure
  }

  test("traverse() when resolves to Java class and has stats") {
    val ctorPrimary = PrimaryCtors.Empty

    val template = Template(
      early = List(),
      inits = Nil,
      self = TheSelf,
      stats = TheStats
    )
    val regularClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = template
    )

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.Class
    val expectedTemplateContext = TemplateContext(
      javaScope = expectedChildJavaScope,
      maybeClassName = Some(TheClassName),
      maybePrimaryCtor = Some(ctorPrimary)
    )
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = Nil,
      self = TheTraversedSelf,
      stats = TheTraversedStats
    )
    val expectedTraversedRegularClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = expectedTraversedTemplate
    )

    expectResolveJavaTreeType(regularClass, TheScalaMods, expectedJavaTreeType)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(regularClass, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)

    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(regularClass, ClassOrTraitContext(TheParentJavaScope)).structure shouldBe expectedTraversedRegularClass.structure
  }

  test("traverse() when resolves to Java class and has one ctor arg list + stats") {
    val ctorPrimary = ctorPrimaryOf(List(CtorArgList1))
    val initialTemplate = Template(
      early = List(),
      inits = Nil,
      self = TheSelf,
      stats = TheStats
    )
    val regularClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = initialTemplate
    )

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.Class
    val expectedTemplateContext = TemplateContext(
      javaScope = expectedChildJavaScope,
      maybeClassName = Some(TheClassName),
      maybePrimaryCtor = Some(ctorPrimary)
    )
    val expectedSyntheticDeclVars = List(SyntheticDeclVar1, SyntheticDeclVar2)
    val expectedAdjustedTemplate = initialTemplate.copy(stats = expectedSyntheticDeclVars ++ TheStats)
    val expectedSyntheticDeclVarResults = List(SyntheticDeclVar1, SyntheticDeclVar2)
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = Nil,
      self = TheTraversedSelf,
      stats = expectedSyntheticDeclVarResults ++ TheTraversedStats
    )
    val expectedTraversedRegularClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = expectedTraversedTemplate
    )

    expectResolveJavaTreeType(regularClass, TheScalaMods, expectedJavaTreeType)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(regularClass, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)

    when(paramToDeclVarTransformer.transform(any[Term.Param])).thenAnswer((ctorArg: Term.Param) => ctorArg match {
      case arg if arg.structure == CtorArg1.structure => SyntheticDeclVar1
      case arg if arg.structure == CtorArg2.structure => SyntheticDeclVar2
    })

    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(expectedAdjustedTemplate), eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(regularClass, ClassOrTraitContext(TheParentJavaScope)).structure shouldBe expectedTraversedRegularClass.structure
  }

  test("traverse() when resolves to Java class and has two list of ctor args - should create all synthetic vars") {
    val ctorPrimary = ctorPrimaryOf(List(CtorArgList1, CtorArgList2))
    val initialTemplate = Template(
      early = List(),
      inits = Nil,
      self = TheSelf,
      stats = Nil
    )
    val regularClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = initialTemplate
    )

    val expectedJavaTreeType = JavaTreeType.Class
    val expectedChildJavaScope = JavaScope.Class
    val expectedTemplateContext = TemplateContext(
      javaScope = expectedChildJavaScope,
      maybeClassName = Some(TheClassName),
      maybePrimaryCtor = Some(ctorPrimary)
    )
    val expectedSyntheticDeclVars = List(
      SyntheticDeclVar1,
      SyntheticDeclVar2,
      SyntheticDeclVar3,
      SyntheticDeclVar4
    )
    val expectedAdjustedTemplate = initialTemplate.copy(stats = expectedSyntheticDeclVars)
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = Nil,
      self = TheTraversedSelf,
      stats = expectedSyntheticDeclVars
    )
    val expectedTraversedRegularClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = expectedTraversedTemplate
    )

    expectResolveJavaTreeType(regularClass, TheScalaMods, expectedJavaTreeType)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(regularClass, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)

    when(paramToDeclVarTransformer.transform(any[Term.Param])).thenAnswer((ctorArg: Term.Param) => ctorArg match {
      case arg if arg.structure == CtorArg1.structure => SyntheticDeclVar1
      case arg if arg.structure == CtorArg2.structure => SyntheticDeclVar2
      case arg if arg.structure == CtorArg3.structure => SyntheticDeclVar3
      case arg if arg.structure == CtorArg4.structure => SyntheticDeclVar4
    })

    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(expectedAdjustedTemplate), eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(regularClass, ClassOrTraitContext(TheParentJavaScope)).structure shouldBe expectedTraversedRegularClass.structure
  }

  test("traverse() when resolves to Java enum") {
    val ctorPrimary = PrimaryCtors.Empty
    val enumConstantVar = q"final var First, Second = Value"
    val template = Template(
      early = List(),
      inits = Nil,
      self = Selfs.Empty,
      stats = List(enumConstantVar)
    )
    val regularClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = template
    )

    val expectedJavaTreeType = JavaTreeType.Enum
    val expectedChildJavaScope = JavaScope.Enum
    val expectedTemplateContext = TemplateContext(
      javaScope = expectedChildJavaScope,
      maybeClassName = Some(TheClassName),
      maybePrimaryCtor = Some(ctorPrimary)
    )
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = Nil,
      self = Selfs.Empty,
      stats = List(enumConstantVar)
    )
    val expectedTraversedRegularClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = expectedTraversedTemplate
    )

    expectResolveJavaTreeType(regularClass, TheScalaMods, expectedJavaTreeType)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    when(javaChildScopeResolver.resolve(eqJavaChildScopeContext(JavaChildScopeContext(regularClass, expectedJavaTreeType))))
      .thenReturn(expectedChildJavaScope)

    doReturn(expectedTraversedTemplate)
      .when(templateTraverser).traverse(eqTree(template), eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(regularClass, ClassOrTraitContext(TheParentJavaScope)).structure shouldBe expectedTraversedRegularClass.structure
  }

  private def ctorPrimaryOf(paramss: List[List[Term.Param]] = Nil) = {
    Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = paramss
    )
  }

  private def expectResolveJavaTreeType(defnClass: Defn.Class, scalaMods: List[Mod], expectedJavaTreeType: JavaTreeType): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(defnClass, scalaMods)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(expectedJavaTreeType)
  }
}
