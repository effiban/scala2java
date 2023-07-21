package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.renderers.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers.matchers.TemplateRenderContextMatcher.eqTemplateRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.Selfs
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Defn, Mod, Template, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm}

class ObjectRendererImplTest extends UnitTestSuite {

  private val ObjectName = q"MyObject"

  private val ScalaMods: List[Mod] = List(mod"@MyAnnotation")

  private val Inits = List(init"Parent1()", init"Parent2()")

  private val Statement1 = q"def myMethod1(x: Int): String"
  private val Statement2 = q"def myMethod2(y: Int): String"

  private val modListRenderer = mock[ModListRenderer]
  private val templateRenderer = mock[TemplateRenderer]


  private val objectRenderer = new ObjectRendererImpl(
    modListRenderer,
    templateRenderer
  )

  test("render() for class with parents") {
    val template = Template(
      early = List(),
      inits = Inits,
      self = Selfs.Empty,
      stats = List(Statement1, Statement2)
    )

    val defnObject = Defn.Object(
      mods = ScalaMods,
      name = ObjectName,
      templ = template
    )

    val javaModifiers = List(JavaModifier.Public, JavaModifier.Final)
    val inheritanceKeyword = JavaKeyword.Extends
    val templateBodyContext = TemplateBodyRenderContext(
      Map(
        Statement1 -> EmptyStatRenderContext,
        Statement2 -> EmptyStatRenderContext
      )
    )
    val objectContext = ObjectRenderContext(
      javaModifiers = javaModifiers,
      javaTypeKeyword = JavaKeyword.Class,
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
        |public final """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersContext))
    doWrite(
      """ implements Parent1, Parent2 {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateRenderer).render(
      eqTree(template),
      eqTemplateRenderContext(expectedTemplateContext)
    )

    objectRenderer.render(defnObject, objectContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public final class MyObject implements Parent1, Parent2 {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render() for utility class") {
    val template = Template(
      early = List(),
      inits = Nil,
      self = Selfs.Empty,
      stats = List(Statement1, Statement2)
    )

    val defnObject = Defn.Object(
      mods = ScalaMods,
      name = ObjectName,
      templ = template
    )

    val javaModifiers = List(JavaModifier.Public, JavaModifier.Final)
    val templateBodyContext = TemplateBodyRenderContext(
      Map(
        Statement1 -> EmptyStatRenderContext,
        Statement2 -> EmptyStatRenderContext
      )
    )
    val objectContext = ObjectRenderContext(
      javaModifiers = javaModifiers,
      javaTypeKeyword = JavaKeyword.Class,
      bodyContext = templateBodyContext
    )

    val expectedModifiersContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = javaModifiers)
    val expectedTemplateContext = TemplateRenderContext(bodyContext = templateBodyContext)

    doWrite(
      """@MyAnnotation
        |public final """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersContext))
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateRenderer).render(
      eqTree(template),
      eqTemplateRenderContext(expectedTemplateContext)
    )

    objectRenderer.render(defnObject, objectContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public final class MyObject {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render() for enum") {
    val enumTypeDef = q"type MyEnum = Value"
    val enumConstantListDef = q"val First, Second = Value"
    val template = Template(
      early = List(),
      inits = Nil,
      self = Selfs.Empty,
      stats = List(enumConstantListDef)
    )

    val defnObject = Defn.Object(
      mods = ScalaMods,
      name = q"MyEnum",
      templ = template
    )

    val javaModifiers = List(JavaModifier.Public)
    val templateBodyContext = TemplateBodyRenderContext(
      Map(
        enumTypeDef -> EmptyStatRenderContext,
        enumConstantListDef -> EnumConstantListRenderContext
      )
    )
    val objectContext = ObjectRenderContext(
      javaModifiers = javaModifiers,
      javaTypeKeyword = JavaKeyword.Enum,
      bodyContext = templateBodyContext
    )

    val expectedModifiersContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = javaModifiers)
    val expectedTemplateContext = TemplateRenderContext(bodyContext = templateBodyContext)

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersContext))
    doWrite(
      """ {
        |  First, Second;
        |}
        |""".stripMargin)
      .when(templateRenderer).render(
      eqTree(template),
      eqTemplateRenderContext(expectedTemplateContext)
    )

    objectRenderer.render(defnObject, objectContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public enum MyEnum {
        |  First, Second;
        |}
        |""".stripMargin
  }
}
