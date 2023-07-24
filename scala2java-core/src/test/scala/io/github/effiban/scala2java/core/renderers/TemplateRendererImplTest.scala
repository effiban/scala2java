package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaKeyword
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.renderers.matchers.TemplateBodyRenderContextMockitoMatcher.eqTemplateBodyRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{Selfs, Templates}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Name, Self, Stat, Template, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TemplateRendererImplTest extends UnitTestSuite {
  private val Init1 = init"Parent1()"
  private val Init2 = init"Parent2()"
  private val TheInits = List(Init1, Init2)

  private val NonEmptySelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(t"SelfType"))

  private val PermittedSubTypeNames = List(t"Child1", q"Child2")

  private val DefnVar = q"var y = 4"

  private val DefnDef = q"def myMethod(x: Int) = doSomething(x)"

  private val initListRenderer = mock[InitListRenderer]
  private val selfRenderer = mock[SelfRenderer]
  private val templateBodyRenderer = mock[TemplateBodyRenderer]
  private val permittedSubTypeNameListRenderer = mock[PermittedSubTypeNameListRenderer]

  private val templateRenderer = new TemplateRendererImpl(
    initListRenderer,
    selfRenderer,
    templateBodyRenderer,
    permittedSubTypeNameListRenderer
  )

  test("render when empty") {
    expectRenderSelf()
    expectRenderBody()

    templateRenderer.render(template = Templates.Empty)

    outputWriter.toString shouldBe
      """ {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render when has inits only") {
    val template = Template(
      early = Nil,
      inits = TheInits,
      self = Selfs.Empty,
      stats = Nil
    )
    val context = TemplateRenderContext(maybeInheritanceKeyword = Some(JavaKeyword.Implements))

    expectRenderInits()
    expectRenderSelf()
    expectRenderBody(context = context)

    templateRenderer.render(template, context)

    outputWriter.toString shouldBe
      """ implements Parent1, Parent2 {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render when has self only") {
    val template = Template(
      early = Nil,
      inits = Nil,
      self = NonEmptySelf,
      stats = Nil
    )

    expectRenderSelf(NonEmptySelf)
    expectRenderBody()

    templateRenderer.render(template)

    outputWriter.toString shouldBe
      """/* extends SelfName: SelfType */ {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render when has permitted subtypes only") {
    val template = Template(
      early = Nil,
      inits = Nil,
      self = Selfs.Empty,
      stats = Nil
    )
    val context = TemplateRenderContext(permittedSubTypeNames = PermittedSubTypeNames)

    expectRenderSelf()
    expectRenderPermittedSubTypeNames()
    expectRenderBody(context = context)

    templateRenderer.render(template, context)

    outputWriter.toString shouldBe
      """ permits Child1, Child2 {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse when has stats only") {
    val stats = List(
      DefnVar,
      DefnDef,
    )

    val template = Template(
      early = Nil,
      inits = Nil,
      self = Selfs.Empty,
      stats = stats
    )
    val bodyContext = TemplateBodyRenderContext(
      Map(
        DefnVar -> VarRenderContext(),
        DefnDef -> DefRenderContext()
      )
    )
    val context = TemplateRenderContext(bodyContext = bodyContext)

    expectRenderSelf()
    expectRenderBody(stats, context)

    templateRenderer.render(template, context)

    outputWriter.toString shouldBe
      """ {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("render when has everything") {
    val stats = List(
      DefnVar,
      DefnDef,
    )

    val template = Template(
      early = Nil,
      inits = TheInits,
      self = NonEmptySelf,
      stats = stats
    )
    val bodyContext = TemplateBodyRenderContext(
      Map(
        DefnVar -> VarRenderContext(),
        DefnDef -> DefRenderContext()
      )
    )
    val context = TemplateRenderContext(
      maybeInheritanceKeyword = Some(JavaKeyword.Implements),
      permittedSubTypeNames = PermittedSubTypeNames,
      bodyContext = bodyContext
    )

    expectRenderInits()
    expectRenderSelf(NonEmptySelf)
    expectRenderPermittedSubTypeNames()
    expectRenderBody(stats, context)

    templateRenderer.render(template, context)

    outputWriter.toString shouldBe
      """ implements Parent1, Parent2/* extends SelfName: SelfType */ permits Child1, Child2 {
        |  /* BODY */
        |}
        |""".stripMargin

  }

  private def expectRenderInits(): Unit = {
    doWrite("Parent1, Parent2")
      .when(initListRenderer).render(eqTreeList(TheInits), eqTo(InitRenderContext(ignoreArgs = true)))
  }


  private def expectRenderSelf(self: Self = Selfs.Empty): Unit = {
    val selfStr = self match {
      case slf if slf.structure == Selfs.Empty.structure => ""
      case _ => "/* extends SelfName: SelfType */"
    }
    doWrite(selfStr).when(selfRenderer).render(eqTree(self))
  }

  private def expectRenderPermittedSubTypeNames(): Unit = {
    doWrite("permits Child1, Child2")
      .when(permittedSubTypeNameListRenderer).render(eqTreeList(PermittedSubTypeNames))
  }

  private def expectRenderBody(stats: List[Stat] = Nil, context: TemplateRenderContext = TemplateRenderContext()): Unit = {
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateBodyRenderer).render(
      eqTreeList(stats),
      eqTemplateBodyRenderContext(context.bodyContext))
  }

}
