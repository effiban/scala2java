package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModListTraversalResultMockitoMatcher.eqModListTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers._
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{ModListTraversalResult, TermParamTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.{Ctor, Init, Mod, Name, Stat, Term, Type, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType}

class CtorSecondaryTraverserImplTest extends UnitTestSuite {

  private val ClassName = t"MyClass"
  private val TraversedClassName = t"MyTraversedClass"

  private val TheParentInits = List(init"Parent1", init"Parent2")

  private val TheCtorContext = CtorContext(
    javaScope = JavaScope.Class,
    className = ClassName,
    inits = TheParentInits
  )

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val TheScalaMods = List(TheAnnot)

  private val CtorArg1 = param"param1: Int"
  private val CtorArg2 = param"param2: Int"
  private val CtorArg3 = param"param3: Int"
  private val CtorArg4 = param"param4: Int"

  private val CtorArgList1 = List(CtorArg1, CtorArg2)
  private val CtorArgList2 = List(CtorArg3, CtorArg4)

  private val TraversedCtorArg1 = param"param11: Int"
  private val TraversedCtorArg2 = param"param22: Int"
  private val TraversedCtorArg3 = param"param33: Int"
  private val TraversedCtorArg4 = param"param44: Int"

  private val TraversedCtorArgList1 = List(TraversedCtorArg1, TraversedCtorArg2)
  private val TraversedCtorArgList2 = List(TraversedCtorArg3, TraversedCtorArg4)

  private val TheSelfInit = init"this(param1)"
  private val TheTraversedSelfInit = init"this(param11)"

  private val Statement1 = q"doSomething1(param1)"
  private val Statement2 = q"doSomething2(param2)"

  private val TraversedStatement1 = q"doSomething11(param11)"
  private val TraversedStatement2 = q"doSomething22(param22)"

  private val statModListTraverser = mock[StatModListTraverser]
  private val modifiersRenderContextFactory = mock[ModifiersRenderContextFactory]
  private val modListRenderer = mock[ModListRenderer]
  private val typeNameTraverser = mock[TypeNameTraverser]
  private val typeNameRenderer = mock[TypeNameRenderer]
  private val termParamTraverser = mock[TermParamTraverser]
  private val termParamListRenderer = mock[TermParamListRenderer]
  private val initTraverser = mock[InitTraverser]
  private val initRenderer = mock[InitRenderer]
  private val blockStatTraverser = mock[BlockStatTraverser]
  private val blockStatRenderer = mock[BlockStatRenderer]

  private val ctorSecondaryTraverser = new CtorSecondaryTraverserImpl(
    statModListTraverser,
    modifiersRenderContextFactory,
    modListRenderer,
    typeNameTraverser,
    typeNameRenderer,
    termParamTraverser, 
    termParamListRenderer,
    initTraverser,
    initRenderer,
    blockStatTraverser,
    blockStatRenderer
  )

  test("traverse() with no statements") {
    val javaScope = JavaScope.Class

    val ctorSecondary = Ctor.Secondary(
      mods = TheScalaMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1),
      init = TheSelfInit,
      stats = Nil
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = TheScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = TheScalaMods, javaModifiers = List(JavaModifier.Public))

    doReturn(expectedModListTraversalResult).when(statModListTraverser).traverse(eqExpectedScalaMods(ctorSecondary, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TraversedClassName).when(typeNameTraverser).traverse(eqTree(ClassName))
    doWrite("MyTraversedClass").when(typeNameRenderer).render(eqTree(TraversedClassName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
        case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
        case aParam => aParam
      }
      TermParamTraversalResult(traversedParam, List(JavaModifier.Final))
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedCtorArgList1),
      context = eqTo(TermParamListRenderContext(List(JavaModifier.Final)))
    )
    doReturn(TheTraversedSelfInit).when(initTraverser).traverse(eqTree(TheSelfInit))
    doWrite("  this(param11)").when(initRenderer).render(eqTree(TheTraversedSelfInit), eqTo(InitContext(argNameAsComment = true)))

    ctorSecondaryTraverser.traverse(ctorSecondary, TheCtorContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public MyTraversedClass(final int param11, final int param22) {
        |  this(param11);
        |}
        |""".stripMargin
  }

  test("traverse() with statements") {
    val javaScope = JavaScope.Class

    val ctorSecondary = Ctor.Secondary(
      mods = TheScalaMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1),
      init = TheSelfInit,
      stats = List(Statement1, Statement2)
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = TheScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = TheScalaMods, javaModifiers = List(JavaModifier.Public))

    doReturn(expectedModListTraversalResult).when(statModListTraverser).traverse(eqExpectedScalaMods(ctorSecondary, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TraversedClassName).when(typeNameTraverser).traverse(eqTree(ClassName))
    doWrite("MyTraversedClass").when(typeNameRenderer).render(eqTree(TraversedClassName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
        case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
        case aParam => aParam
      }
      TermParamTraversalResult(traversedParam, List(JavaModifier.Final))
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedCtorArgList1),
      context = eqTo(TermParamListRenderContext(List(JavaModifier.Final)))
    )
    doReturn(TheTraversedSelfInit).when(initTraverser).traverse(eqTree(TheSelfInit))
    doWrite("  this(param11)").when(initRenderer).render(eqTree(TheTraversedSelfInit), eqTo(InitContext(argNameAsComment = true)))

    doAnswer((stat: Stat) => stat match {
      case aStat if aStat.structure == Statement1.structure => TraversedStatement1
      case aStat if aStat.structure == Statement2.structure => TraversedStatement2
      case aStat => aStat
    }).when(blockStatTraverser).traverse(any[Stat])
    doWrite(
      """  doSomething11(param11);
        |""".stripMargin).when(blockStatRenderer).render(eqTree(TraversedStatement1))
    doWrite(
      """  doSomething22(param22);
        |""".stripMargin).when(blockStatRenderer).render(eqTree(TraversedStatement2))

    ctorSecondaryTraverser.traverse(ctorSecondary, TheCtorContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public MyTraversedClass(final int param11, final int param22) {
        |  this(param11);
        |  doSomething11(param11);
        |  doSomething22(param22);
        |}
        |""".stripMargin
  }

  test("traverse() with two argument lists") {
    val javaScope = JavaScope.Class

    val ctorSecondary = Ctor.Secondary(
      mods = TheScalaMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1, CtorArgList2),
      init = TheSelfInit,
      stats = Nil
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = TheScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = TheScalaMods, javaModifiers = List(JavaModifier.Public))

    doReturn(expectedModListTraversalResult).when(statModListTraverser).traverse(eqExpectedScalaMods(ctorSecondary, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TraversedClassName).when(typeNameTraverser).traverse(eqTree(ClassName))
    doWrite("MyTraversedClass").when(typeNameRenderer).render(eqTree(TraversedClassName))
    doAnswer((param: Term.Param) => {
      val traversedParam = param match {
        case aParam if aParam.structure == CtorArg1.structure => TraversedCtorArg1
        case aParam if aParam.structure == CtorArg2.structure => TraversedCtorArg2
        case aParam if aParam.structure == CtorArg3.structure => TraversedCtorArg3
        case aParam if aParam.structure == CtorArg4.structure => TraversedCtorArg4
        case aParam => aParam
      }
      TermParamTraversalResult(traversedParam, List(JavaModifier.Final))
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22, final int param33, final int param44)")
      .when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedCtorArgList1 ++ TraversedCtorArgList2),
      context = eqTo(TermParamListRenderContext(List(JavaModifier.Final)))
    )
    doReturn(TheTraversedSelfInit).when(initTraverser).traverse(eqTree(TheSelfInit))
    doWrite("  this(param11)").when(initRenderer).render(eqTree(TheTraversedSelfInit), eqTo(InitContext(argNameAsComment = true)))

    ctorSecondaryTraverser.traverse(ctorSecondary, TheCtorContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public MyTraversedClass(final int param11, final int param22, final int param33, final int param44) {
        |  this(param11);
        |}
        |""".stripMargin
  }

  private def eqExpectedScalaMods(ctorSecondary: Ctor.Secondary, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(ctorSecondary, JavaTreeType.Method, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
