package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.renderers.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers.matchers.TemplateRenderContextMatcher.eqTemplateRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.Selfs
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Defn, Mod, Name, Template, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class CaseClassRendererImplTest extends UnitTestSuite {

  private val ClassName = t"MyRecord"

  private val ScalaMods: List[Mod] = List(mod"@MyAnnotation", Mod.Case())

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val CtorArg1 = param"arg1: Int"
  private val CtorArg2 = param"arg2: Int"
  private val CtorArg3 = param"arg3: Int"
  private val CtorArg4 = param"arg4: Int"

  private val CtorArgList1 = List(CtorArg1, CtorArg2)
  private val CtorArgList2 = List(CtorArg3, CtorArg4)

  private val Inits = List(init"Parent1()", init"Parent2()")

  private val Statement1 = q"def myMethod1(x: Int): String"
  private val Statement2 = q"def myMethod2(y: Int): String"

  private val TheTemplate =
    Template(
      early = List(),
      inits = Inits,
      self = Selfs.Empty,
      stats = List(Statement1, Statement2)
    )

  private val modListRenderer = mock[ModListRenderer]
  private val typeParamListRenderer = mock[TypeParamListRenderer]
  private val termParamListRenderer = mock[TermParamListRenderer]
  private val templateRenderer = mock[TemplateRenderer]


  private val caseClassRenderer = new CaseClassRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    termParamListRenderer,
    templateRenderer
  )

  test("render() when has one argument list and no parents") {
    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgList1))
    val caseClass = Defn.Class(
      mods = ScalaMods,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )
    val javaModifiers = List(JavaModifier.Public)
    val templateBodyContext = TemplateBodyRenderContext(
      Map(
        Statement1 -> EmptyStatRenderContext,
        Statement2 -> EmptyStatRenderContext
      )
    )
    val caseClassContext = CaseClassRenderContext(
      javaModifiers = javaModifiers,
      bodyContext = templateBodyContext
    )

    val expectedModifiersContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = javaModifiers)
    val expectedTemplateContext = TemplateRenderContext(bodyContext = templateBodyContext)

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersContext))
    doWrite("<T1, T2>").when(typeParamListRenderer).render(eqTreeList(TypeParams))
    doWrite("(int arg1, int arg2)").when(termParamListRenderer).render(
      termParams = eqTreeList(CtorArgList1),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateRenderer).render(
      eqTree(TheTemplate),
      eqTemplateRenderContext(expectedTemplateContext)
    )

    caseClassRenderer.render(caseClass, caseClassContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public record MyRecord<T1, T2>(int arg1, int arg2) {
        |  /* BODY */
        |}
        |""".stripMargin
  }


  test("render() when has one argument list and parents") {
    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgList1))
    val caseClass = Defn.Class(
      mods = ScalaMods,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )
    val javaModifiers = List(JavaModifier.Public)
    val inheritanceKeyword = JavaKeyword.Implements
    val templateBodyContext = TemplateBodyRenderContext(
      Map(
        Statement1 -> EmptyStatRenderContext,
        Statement2 -> EmptyStatRenderContext
      )
    )
    val caseClassContext = CaseClassRenderContext(
      javaModifiers = javaModifiers,
      maybeInheritanceKeyword = Some(inheritanceKeyword),
      bodyContext = templateBodyContext
    )

    val expectedModifiersContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = javaModifiers)
    val expectedTemplateContext = TemplateRenderContext(
      maybeInheritanceKeyword = Some(inheritanceKeyword),
      bodyContext = templateBodyContext
    )

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersContext))
    doWrite("<T1, T2>").when(typeParamListRenderer).render(eqTreeList(TypeParams))
    doWrite("(int arg1, int arg2)").when(termParamListRenderer).render(
      termParams = eqTreeList(CtorArgList1),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite(
      """ implements Parent1, Parent2 {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateRenderer).render(
      eqTree(TheTemplate),
      eqTemplateRenderContext(expectedTemplateContext)
    )

    caseClassRenderer.render(caseClass, caseClassContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public record MyRecord<T1, T2>(int arg1, int arg2) implements Parent1, Parent2 {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render() when has two argument lists") {
    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgList1 ++ CtorArgList2))
    val caseClass = Defn.Class(
      mods = ScalaMods,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )
    val javaModifiers = List(JavaModifier.Public)
    val templateBodyContext = TemplateBodyRenderContext(
      Map(
        Statement1 -> EmptyStatRenderContext,
        Statement2 -> EmptyStatRenderContext
      )
    )
    val caseClassContext = CaseClassRenderContext(
      javaModifiers = javaModifiers,
      bodyContext = templateBodyContext
    )

    val expectedModifiersContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = javaModifiers)
    val expectedTemplateContext = TemplateRenderContext(bodyContext = templateBodyContext)

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersContext))
    doWrite("<T1, T2>").when(typeParamListRenderer).render(eqTreeList(TypeParams))
    doWrite("(int arg1, int arg2, int arg3, int arg4)").when(termParamListRenderer).render(
      termParams = eqTreeList(CtorArgList1 ++ CtorArgList2),
      context = eqTo(TermParamListRenderContext())
    )
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateRenderer).render(
      eqTree(TheTemplate),
      eqTemplateRenderContext(expectedTemplateContext)
    )

    caseClassRenderer.render(caseClass, caseClassContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public record MyRecord<T1, T2>(int arg1, int arg2, int arg3, int arg4) {
        |  /* BODY */
        |}
        |""".stripMargin
  }
}
