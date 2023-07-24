package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.renderers.matchers.StatRenderContextMockitoMatcher.eqStatRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Pkg, XtensionQuasiquoteTerm}

class PkgRendererImplTest extends UnitTestSuite {

  private val Import1 = q"import pkg1.class1"
  private val Import2 = q"import pkg2.class2"

  private val TheDefnVar = q"final var x: Int = 3"

  private val TheDefnDef = q"def foo(y: Int) = y + 2"

  private val TheClass =
    q"""
    class MyClass {
      final var x: Int = 3

      def foo(y: Int) = y + 2
    }
    """

  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]
  private val defaultStatRenderer = mock[DefaultStatRenderer]

  private val pkgRenderer = new PkgRendererImpl(defaultTermRefRenderer, defaultStatRenderer)


  test("render()") {
    val pkgRef = q"mypkg.myinnerpkg"
    val stats = List(Import1, Import2, TheClass)
    val pkg = Pkg(ref = pkgRef, stats = stats)

    val classContext = RegularClassRenderContext(
      javaModifiers = List(JavaModifier.Public),
      bodyContext = TemplateBodyRenderContext(statContextMap =
        Map(
          TheDefnVar -> VarRenderContext(List(JavaModifier.Private, JavaModifier.Final)),
          TheDefnDef -> DefRenderContext(List(JavaModifier.Public))
        )
      )
    )

    val context = PkgRenderContext(
      Map(
        Import1 -> EmptyStatRenderContext,
        Import2 -> EmptyStatRenderContext,
        TheClass -> classContext
      )
    )

    doWrite("mytraversedpkg.myinnerpkg").when(defaultTermRefRenderer).render(eqTree(pkgRef))
    doWrite(
      """import pkg1.class1;
         |""".stripMargin)
      .when(defaultStatRenderer).render(eqTree(Import1), eqTo(EmptyStatRenderContext))
    doWrite(
      """import pkg2.class2;
        |""".stripMargin)
      .when(defaultStatRenderer).render(eqTree(Import2), eqTo(EmptyStatRenderContext))
    doWrite(
      """
        |public class MyClass {
        |  private final int x = 3;
        |
        |  public int foo(int y) = {
        |    return y + 2;
        |  }
        |}
        |""".stripMargin
    ).when(defaultStatRenderer).render(eqTree(TheClass), eqStatRenderContext(classContext))

    pkgRenderer.render(pkg, context)

    outputWriter.toString shouldBe
      """package mytraversedpkg.myinnerpkg;
        |
        |import pkg1.class1;
        |import pkg2.class2;
        |
        |public class MyClass {
        |  private final int x = 3;
        |
        |  public int foo(int y) = {
        |    return y + 2;
        |  }
        |}
        |""".stripMargin
  }
}
