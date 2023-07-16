package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.InitContext
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers.contexts.{CtorSecondaryRenderContext, ModifiersRenderContext, TermParamListRenderContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Init, Mod, Name, Type, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType}

class CtorSecondaryRendererImplTest extends UnitTestSuite {

  private val ClassName = t"MyClass"

  private val TheParentInits = List(init"Parent1", init"Parent2")

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val TheScalaMods = List(TheAnnot)

  private val TheJavaModifiers = List(JavaModifier.Public)

  private val TheContext = CtorSecondaryRenderContext(
    className = ClassName,
    javaModifiers = TheJavaModifiers
  )
  private val CtorArg1 = param"param1: Int"
  private val CtorArg2 = param"param2: Int"
  private val CtorArg3 = param"param3: Int"
  private val CtorArg4 = param"param4: Int"

  private val CtorArgList1 = List(CtorArg1, CtorArg2)
  private val CtorArgList2 = List(CtorArg3, CtorArg4)

  private val TheSelfInit = init"this(param1)"

  private val Statement1 = q"doSomething1(param1)"
  private val Statement2 = q"doSomething2(param2)"


  private val modListRenderer = mock[ModListRenderer]
  private val typeNameRenderer = mock[TypeNameRenderer]
  private val termParamListRenderer = mock[TermParamListRenderer]
  private val initRenderer = mock[InitRenderer]
  private val blockStatRenderer = mock[BlockStatRenderer]

  private val ctorSecondaryRenderer = new CtorSecondaryRendererImpl(
    modListRenderer,
    typeNameRenderer,
    termParamListRenderer,
    initRenderer,
    blockStatRenderer
  )

  test("render() with no statements") {
    val ctorSecondary = Ctor.Secondary(
      mods = TheScalaMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1),
      init = TheSelfInit,
      stats = Nil
    )

    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = TheScalaMods, javaModifiers = List(JavaModifier.Public))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("MyClass").when(typeNameRenderer).render(eqTree(ClassName))
    doWrite("(final int param1, final int param2)").when(termParamListRenderer).render(
      termParams = eqTreeList(CtorArgList1),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite("  this(param1)").when(initRenderer).render(eqTree(TheSelfInit), eqTo(InitContext(argNameAsComment = true)))

    ctorSecondaryRenderer.render(ctorSecondary, TheContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public MyClass(final int param1, final int param2) {
        |  this(param1);
        |}
        |""".stripMargin

  }

  test("render() with statements") {
    val ctorSecondary = Ctor.Secondary(
      mods = TheScalaMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1),
      init = TheSelfInit,
      stats = List(Statement1, Statement2)
    )

    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = TheScalaMods, javaModifiers = List(JavaModifier.Public))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("MyClass").when(typeNameRenderer).render(eqTree(ClassName))
    doWrite("(final int param1, final int param2)").when(termParamListRenderer).render(
      termParams = eqTreeList(CtorArgList1),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite("  this(param1)").when(initRenderer).render(eqTree(TheSelfInit), eqTo(InitContext(argNameAsComment = true)))
    doWrite(
      """  doSomething1(param1);
        |""".stripMargin).when(blockStatRenderer).render(eqTree(Statement1))
    doWrite(
      """  doSomething2(param2);
        |""".stripMargin).when(blockStatRenderer).render(eqTree(Statement2))

    ctorSecondaryRenderer.render(ctorSecondary, TheContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public MyClass(final int param1, final int param2) {
        |  this(param1);
        |  doSomething1(param1);
        |  doSomething2(param2);
        |}
        |""".stripMargin
  }

  test("render() with two argument lists") {
    val ctorSecondary = Ctor.Secondary(
      mods = TheScalaMods,
      name = Name.Anonymous(),
      paramss = List(CtorArgList1, CtorArgList2),
      init = TheSelfInit,
      stats = Nil
    )

    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = TheScalaMods, javaModifiers = List(JavaModifier.Public))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("MyClass").when(typeNameRenderer).render(eqTree(ClassName))
    doWrite("(final int param1, final int param2, final int param3, final int param4)")
      .when(termParamListRenderer).render(
      termParams = eqTreeList(CtorArgList1 ++ CtorArgList2),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite("  this(param1)").when(initRenderer).render(eqTree(TheSelfInit), eqTo(InitContext(argNameAsComment = true)))

    ctorSecondaryRenderer.render(ctorSecondary, TheContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public MyClass(final int param1, final int param2, final int param3, final int param4) {
        |  this(param1);
        |}
        |""".stripMargin

  }
}
