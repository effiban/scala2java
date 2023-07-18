package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.renderers.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers.matchers.TemplateRenderContextMatcher.eqTemplateRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Selfs}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Defn, Mod, Template, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TraitRendererImplTest extends UnitTestSuite {

  private val TraitName = t"MyTrait"

  private val ScalaMods: List[Mod] = List(mod"@MyAnnotation")

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val Inits = List(init"Parent1()", init"Parent2()")

  private val PermittedSubTypeNames = List(t"Child1", t"Child2")

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
  private val templateRenderer = mock[TemplateRenderer]


  private val traitRenderer = new TraitRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    templateRenderer
  )

  test("render()") {
    val `trait` = Defn.Trait(
      mods = ScalaMods,
      name = TraitName,
      tparams = TypeParams,
      ctor = PrimaryCtors.Empty,
      templ = TheTemplate
    )
    val javaModifiers = List(JavaModifier.Public)
    val templateBodyContext = TemplateBodyRenderContext(
      Map(
        Statement1 -> TemplateStatRenderContext(),
        Statement2 -> TemplateStatRenderContext()
      )
    )
    val traitContext = TraitRenderContext(javaModifiers, PermittedSubTypeNames, templateBodyContext)

    val expectedModifiersContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = javaModifiers)
    val expectedTemplateContext = TemplateRenderContext(
      maybeInheritanceKeyword = Some(JavaKeyword.Extends),
      permittedSubTypeNames = PermittedSubTypeNames,
      bodyContext = templateBodyContext
    )

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersContext))
    doWrite("<T11, T22>").when(typeParamListRenderer).render(eqTreeList(TypeParams))
    doWrite(
      """ extends Parent1, Parent2 permits Child1, Child2 {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateRenderer).render(
      eqTree(TheTemplate),
      eqTemplateRenderContext(expectedTemplateContext)
    )

    traitRenderer.render(`trait`, traitContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public interface MyTrait<T11, T22> extends Parent1, Parent2 permits Child1, Child2 {
        |  /* BODY */
        |}
        |""".stripMargin
  }
}
