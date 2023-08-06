package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}
import io.github.effiban.scala2java.core.matchers.TemplateContextMatcher.eqTemplateContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.{Ctor, Defn, Mod, Name, Self, Template, Term, Type, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class CaseClassTraverserImplTest extends UnitTestSuite {

  private val TheScalaMods = List(mod"@MyAnnotation", Mod.Case())
  private val TheTraversedScalaMods = List(mod"@MyTraversedAnnotation", Mod.Case())

  private val TheClassName = t"MyRecord"

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

  private val TraversedCtorArg1 = param"arg11: Int"
  private val TraversedCtorArg2 = param"arg22: Int"
  private val TraversedCtorArg3 = param"arg33: Int"
  private val TraversedCtorArg4 = param"arg44: Int"

  private val TraversedCtorArgList1 = List(TraversedCtorArg1, TraversedCtorArg2)
  private val TraversedCtorArgList2 = List(TraversedCtorArg3, TraversedCtorArg4)

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
  private val typeParamTraverser = mock[TypeParamTraverser]
  private val termParamTraverser = mock[TermParamTraverser]
  private val templateTraverser = mock[TemplateTraverser]


  private val classTraverser = new CaseClassTraverserImpl(
    statModListTraverser,
    typeParamTraverser,
    termParamTraverser,
    templateTraverser
  )

  test("traverse() for one list of ctor args, basic") {
    val ctorPrimary = Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1)
    )

    val template = Template(
      early = List(),
      inits = List(),
      self = TheSelf,
      stats = TheStats
    )

    val caseClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = template
    )

    val expectedTemplateContext = TemplateContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(TheClassName)
    )
    val expectedJavaModifiers = List(JavaModifier.Public)
    val expectedTraversedCtorPrimary = Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = List(TraversedCtorArgList1)
    )
    val expectedTemplateTraversalResult = TemplateTraversalResult(
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )
    val expectedTraversedCaseClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = expectedTraversedCtorPrimary,
      templ = expectedTemplateTraversalResult.template
    )

    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
      case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.Class)))
    doReturn(expectedTemplateTraversalResult)
      .when(templateTraverser).traverse(
      eqTree(template),
      eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(caseClass, ClassOrTraitContext(JavaScope.Package)).structure shouldBe expectedTraversedCaseClass.structure
  }

  test("traverse() for one list of ctor args, with type params") {
    val ctorPrimary = Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1)
    )

    val template = Template(
      early = List(),
      inits = List(),
      self = TheSelf,
      stats = TheStats
    )

    val caseClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = TheTypeParams,
      ctor = ctorPrimary,
      templ = template
    )

    val expectedTemplateContext = TemplateContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(TheClassName)
    )
    val expectedTraversedCtorPrimary = Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = List(TraversedCtorArgList1)
    )
    val expectedTemplateTraversalResult = TemplateTraversalResult(
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )
    val expectedTraversedCaseClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = TheTraversedTypeParams,
      ctor = expectedTraversedCtorPrimary,
      templ = expectedTemplateTraversalResult.template
    )

    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doAnswer((tparam: Type.Param) => tparam match {
      case aTypeParam if aTypeParam.structure == TypeParam1.structure => TraversedTypeParam1
      case aTypeParam if aTypeParam.structure == TypeParam2.structure => TraversedTypeParam2
      case aTypeParam => aTypeParam
    }).when(typeParamTraverser).traverse(any[Type.Param])
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
      case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.Class)))
    doReturn(expectedTemplateTraversalResult)
      .when(templateTraverser).traverse(
      eqTree(template),
      eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(caseClass, ClassOrTraitContext(JavaScope.Package)).structure shouldBe expectedTraversedCaseClass.structure
  }

  test("traverse() for one list of ctor args with ctor modifiers - should traverse ctor in template") {
    val ctorMods = List(Mod.Private(Name.Anonymous()))
    val ctorPrimary = Ctor.Primary(
      mods = ctorMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1)
    )

    val template = Template(
      early = List(),
      inits = List(),
      self = TheSelf,
      stats = TheStats
    )

    val caseClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = template
    )

    val expectedTemplateContext = TemplateContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(TheClassName),
      maybePrimaryCtor = Some(ctorPrimary)
    )
    val expectedTraversedCtorPrimary = Ctor.Primary(
      mods = ctorMods,
      name = Name.Anonymous(),
      paramss = List(TraversedCtorArgList1)
    )
    val expectedTemplateTraversalResult = TemplateTraversalResult(
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )
    val expectedTraversedCaseClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = expectedTraversedCtorPrimary,
      templ = expectedTemplateTraversalResult.template
    )

    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
      case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.Class)))
    doReturn(expectedTemplateTraversalResult)
      .when(templateTraverser).traverse(
      eqTree(template),
      eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(caseClass, ClassOrTraitContext(JavaScope.Package)).structure shouldBe expectedTraversedCaseClass.structure
  }

  test("traverse() for one list of ctor args, with inits") {
    val ctorPrimary = Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1)
    )

    val template = Template(
      early = List(),
      inits = TheInits,
      self = TheSelf,
      stats = TheStats
    )

    val caseClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = template
    )

    val expectedTemplateContext = TemplateContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(TheClassName)
    )
    val expectedTraversedCtorPrimary = Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = List(TraversedCtorArgList1)
    )
    val expectedTemplateTraversalResult = TemplateTraversalResult(
      maybeInheritanceKeyword = Some(JavaKeyword.Implements),
      inits = TheTraversedInits,
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )
    val expectedTraversedCaseClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = expectedTraversedCtorPrimary,
      templ = expectedTemplateTraversalResult.template
    )

    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
      case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.Class)))
    doReturn(expectedTemplateTraversalResult)
      .when(templateTraverser).traverse(
      eqTree(template),
      eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(caseClass, ClassOrTraitContext(JavaScope.Package)).structure shouldBe expectedTraversedCaseClass.structure
  }

  test("traverse() for two lists of ctor args") {
    val ctorPrimary = Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1, CtorArgList2)
    )

    val template = Template(
      early = List(),
      inits = List(),
      self = TheSelf,
      stats = TheStats
    )

    val caseClass = Defn.Class(
      mods = TheScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = ctorPrimary,
      templ = template
    )

    val expectedTemplateContext = TemplateContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(TheClassName)
    )
    val expectedTraversedCtorPrimary = Ctor.Primary(
      mods = Nil,
      name = Name.Anonymous(),
      paramss = List(TraversedCtorArgList1, TraversedCtorArgList2)
    )
    val expectedTemplateTraversalResult = TemplateTraversalResult(
      self = TheTraversedSelf,
      statResults = TheTraversedStatResults
    )
    val expectedTraversedCaseClass = Defn.Class(
      mods = TheTraversedScalaMods,
      name = TheClassName,
      tparams = Nil,
      ctor = expectedTraversedCtorPrimary,
      templ = expectedTemplateTraversalResult.template
    )

    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
      case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
      case aParam if aParam.structure == CtorArg3.structure => TraversedCtorArg3
      case aParam if aParam.structure == CtorArg4.structure => TraversedCtorArg4
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.Class)))
    doReturn(expectedTemplateTraversalResult)
      .when(templateTraverser).traverse(
      eqTree(template),
      eqTemplateContext(expectedTemplateContext))

    classTraverser.traverse(caseClass, ClassOrTraitContext(JavaScope.Package)).structure shouldBe expectedTraversedCaseClass.structure
  }
}
