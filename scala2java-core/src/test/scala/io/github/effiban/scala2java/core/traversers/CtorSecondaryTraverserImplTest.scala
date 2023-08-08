package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.{Ctor, Init, Mod, Name, Stat, Term, Type, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

class CtorSecondaryTraverserImplTest extends UnitTestSuite {

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val TheTraversedAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyTraversedAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val TheScalaMods = List(TheAnnot)
  private val TheTraversedScalaMods = List(TheTraversedAnnot)

  private val CtorArg1 = param"param1: Int"
  private val CtorArg2 = param"param2: Int"
  private val CtorArg3 = param"param3: Int"
  private val CtorArg4 = param"param4: Int"

  private val CtorArgList1 = List(CtorArg1, CtorArg2)
  private val CtorArgList2 = List(CtorArg3, CtorArg4)

  private val TraversedCtorArg1 = param"final param11: Int"
  private val TraversedCtorArg2 = param"final param22: Int"
  private val TraversedCtorArg3 = param"final param33: Int"
  private val TraversedCtorArg4 = param"final param44: Int"

  private val TraversedCtorArgList1 = List(TraversedCtorArg1, TraversedCtorArg2)
  private val TraversedCtorArgList2 = List(TraversedCtorArg3, TraversedCtorArg4)

  private val TheSelfInit = init"this(param1)"
  private val TheTraversedSelfInit = init"this(param11)"

  private val Statement1 = q"doSomething1(param1)"
  private val Statement2 = q"doSomething2(param2)"

  private val TraversedStatement1 = q"doSomething11(param11)"
  private val TraversedStatement2 = q"doSomething22(param22)"

  private val statModListTraverser = mock[StatModListTraverser]
  private val termParamTraverser = mock[TermParamTraverser]
  private val initTraverser = mock[InitTraverser]
  private val blockStatTraverser = mock[BlockStatTraverser]

  private val ctorSecondaryTraverser = new CtorSecondaryTraverserImpl(
    statModListTraverser,
    termParamTraverser,
    initTraverser,
    blockStatTraverser
  )

  test("traverse() with no statements") {
    val ctorSecondary = Ctor.Secondary(
      mods = TheScalaMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1),
      init = TheSelfInit,
      stats = Nil
    )

    val expectedTraversedCtorSecondary = Ctor.Secondary(
      mods = TheTraversedScalaMods,
      name = Name.Anonymous(),
      paramss = List(TraversedCtorArgList1),
      init = TheTraversedSelfInit,
      stats = Nil
    )

    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
      case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doReturn(TheTraversedSelfInit).when(initTraverser).traverse(eqTree(TheSelfInit))

    ctorSecondaryTraverser.traverse(ctorSecondary).structure shouldBe expectedTraversedCtorSecondary.structure
  }

  test("traverse() with statements") {
    val ctorSecondary = Ctor.Secondary(
      mods = TheScalaMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1),
      init = TheSelfInit,
      stats = List(Statement1, Statement2)
    )

    val expectedTraversedCtorSecondary = Ctor.Secondary(
      mods = TheTraversedScalaMods,
      name = Name.Anonymous(),
      paramss = List(TraversedCtorArgList1),
      init = TheTraversedSelfInit,
      stats = List(TraversedStatement1, TraversedStatement2)
    )

    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
      case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doReturn(TheTraversedSelfInit).when(initTraverser).traverse(eqTree(TheSelfInit))

    doAnswer((stat: Stat) => stat match {
      case aStat if aStat.structure == Statement1.structure => TraversedStatement1
      case aStat if aStat.structure == Statement2.structure => TraversedStatement2
      case aStat => aStat
    }).when(blockStatTraverser).traverse(any[Stat])

    ctorSecondaryTraverser.traverse(ctorSecondary).structure shouldBe expectedTraversedCtorSecondary.structure
  }

  test("traverse() with two argument lists") {
    val ctorSecondary = Ctor.Secondary(
      mods = TheScalaMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1, CtorArgList2),
      init = TheSelfInit,
      stats = Nil
    )

    val expectedTraversedCtorSecondary = Ctor.Secondary(
      mods = TheTraversedScalaMods,
      name = Name.Anonymous(),
      paramss = List(TraversedCtorArgList1, TraversedCtorArgList2),
      init = TheTraversedSelfInit,
      stats = Nil
    )

    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
      case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
      case aParam if aParam.structure == CtorArg3.structure => TraversedCtorArg3
      case aParam if aParam.structure == CtorArg4.structure => TraversedCtorArg4
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doReturn(TheTraversedSelfInit).when(initTraverser).traverse(eqTree(TheSelfInit))

    ctorSecondaryTraverser.traverse(ctorSecondary).structure shouldBe expectedTraversedCtorSecondary.structure
  }
}
