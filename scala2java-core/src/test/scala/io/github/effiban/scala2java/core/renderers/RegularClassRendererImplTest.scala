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

import scala.meta.{Ctor, Defn, Mod, Name, Template, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class RegularClassRendererImplTest extends UnitTestSuite {

  private val ClassName = t"MyClass"

  private val ScalaMods: List[Mod] = List(mod"@MyAnnotation")

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val CtorArg1 = param"arg1: Int"
  private val CtorArg2 = param"arg2: Int"

  private val CtorArgList = List(CtorArg1, CtorArg2)

  private val Inits = List(init"Parent1()", init"Parent2()")

  private val PermittedSubTypeNames = List(t"Child1", t"Child2")

  private val Statement1 = q"def myMethod1(x: Int): String"
  private val Statement2 = q"def myMethod2(y: Int): String"

  private val modListRenderer = mock[ModListRenderer]
  private val typeParamListRenderer = mock[TypeParamListRenderer]
  private val templateRenderer = mock[TemplateRenderer]


  private val regularClassRenderer = new RegularClassRendererImpl(
    modListRenderer,
    typeParamListRenderer,
    templateRenderer
  )

  test("render() for Java class when has no parents and no permitted subtypes") {
    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgList))
    val TheTemplate = Template(
        early = List(),
        inits = Nil,
        self = Selfs.Empty,
        stats = List(Statement1, Statement2)
      )
    val defnClass = Defn.Class(
      mods = ScalaMods,
      name = ClassName,
      tparams = TypeParams,
      ctor = primaryCtor,
      templ = TheTemplate
    )
    val javaModifiers = List(JavaModifier.Public)
    val templateBodyContext = TemplateBodyRenderContext(
      Map(
        Statement1 -> TemplateStatRenderContext(),
        Statement2 -> TemplateStatRenderContext()
      )
    )
    val regularClassContext = RegularClassRenderContext(
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
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateRenderer).render(
      eqTree(TheTemplate),
      eqTemplateRenderContext(expectedTemplateContext)
    )

    regularClassRenderer.render(defnClass, regularClassContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public class MyClass<T1, T2> {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render() for Java class when has parents and permitted subtypes") {
    val primaryCtor = Ctor.Primary(mods = List(), name = Name.Anonymous(), paramss = List(CtorArgList))
    val TheTemplate = Template(
      early = List(),
      inits = Inits,
      self = Selfs.Empty,
      stats = List(Statement1, Statement2)
    )
    val defnClass = Defn.Class(
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
        Statement1 -> TemplateStatRenderContext(),
        Statement2 -> TemplateStatRenderContext()
      )
    )
    val regularClassContext = RegularClassRenderContext(
      javaModifiers = javaModifiers,
      maybeInheritanceKeyword = Some(inheritanceKeyword),
      permittedSubTypeNames = PermittedSubTypeNames,
      bodyContext = templateBodyContext
    )

    val expectedModifiersContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = javaModifiers)
    val expectedTemplateContext = TemplateRenderContext(
      maybeInheritanceKeyword = Some(inheritanceKeyword),
      permittedSubTypeNames = PermittedSubTypeNames,
      bodyContext = templateBodyContext
    )

    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersContext))
    doWrite("<T1, T2>").when(typeParamListRenderer).render(eqTreeList(TypeParams))
    doWrite(
      """ implements Parent1, Parent2 permits Child1, Child2 {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateRenderer).render(
      eqTree(TheTemplate),
      eqTemplateRenderContext(expectedTemplateContext)
    )

    regularClassRenderer.render(defnClass, regularClassContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public class MyClass<T1, T2> implements Parent1, Parent2 permits Child1, Child2 {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render() for Java enum") {
    val enumTypeDef = q"type MyEnum = Value"
    val enumConstantListDef = q"val First, Second = Value"
    val template = Template(
      early = List(),
      inits = Nil,
      self = Selfs.Empty,
      stats = List(enumConstantListDef)
    )

    val defnClass = Defn.Class(
      mods = ScalaMods,
      name = t"MyEnum",
      tparams = TypeParams,
      ctor = PrimaryCtors.Empty,
      templ = template
    )

    val javaModifiers = List(JavaModifier.Public)
    val templateBodyContext = TemplateBodyRenderContext(
      Map(
        enumTypeDef -> TemplateStatRenderContext(),
        enumConstantListDef -> TemplateStatRenderContext(enumConstantList = true)
      )
    )
    val regularClassContext = RegularClassRenderContext(
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

    regularClassRenderer.render(defnClass, regularClassContext)

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public enum MyEnum {
        |  First, Second;
        |}
        |""".stripMargin
  }
}
