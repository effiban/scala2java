package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.renderers.matchers.PkgRenderContextMockitoMatcher.eqPkgRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteSource, XtensionQuasiquoteTerm}

class SourceRendererImplTest extends UnitTestSuite {

  private val Method1 = q"def method1(x: Int) = x + 2"
  private val Method2 = q"def method2(x: Int) = x + 4"

  private val Class1 =
    q"""
    class Class1 {
      def method1(x: Int) = x + 2
    }
    """
  private val Class2 =
    q"""
    class Class2 {
      def method2(x: Int) = x + 4
    }
    """

  private val Pkg1 =
    q"""
    package pkg1 {
      class Class1 {
        def method1(x: Int) = x + 2
      }
    }
    """
  private val Pkg2 =
    q"""
    package pkg2 {
      class Class2 {
        def method2(x: Int) = x + 4
      }
    }
    """

  // Java doesn't support multiple packages in a file, but in this test I still want to verify that multiple statements work
  private val TheSource =
    source"""
    package pkg1 {
      class Class1 {
        def method1(x: Int) = x + 2
      }
    }

    package pkg2 {
      class Class2 {
        def method2(x: Int) = x + 4
      }
    }
    """

  private val defaultStatRenderer = mock[DefaultStatRenderer]

  private val sourceRenderer = new SourceRendererImpl(defaultStatRenderer)


  test("render()") {
    val stats = List(Pkg1, Pkg2)

    val class1Context = RegularClassRenderContext(
      javaModifiers = List(JavaModifier.Public),
      bodyContext = TemplateBodyRenderContext(statContextMap =
        Map(Method1 -> DefRenderContext(List(JavaModifier.Public)))
      )
    )
    val class2Context = RegularClassRenderContext(
      javaModifiers = List(JavaModifier.Public),
      bodyContext = TemplateBodyRenderContext(statContextMap =
        Map(Method2 -> DefRenderContext(List(JavaModifier.Public)))
      )
    )

    val pkg1Context = PkgRenderContext(
      Map(
        Class1 -> class1Context
      )
    )
    val pkg2Context = PkgRenderContext(
      Map(
        Class2 -> class2Context
      )
    )

    val sourceContext = SourceRenderContext(
      Map(
        Pkg1 -> pkg1Context,
        Pkg2 -> pkg2Context
      )
    )

    doWrite(
      """package pkg1;
        |
        |public class Class1 {
        |  public int method1(final int x) {
        |    return x + 2;
        |  }
        |}
        |""".stripMargin)
      .when(defaultStatRenderer).render(eqTree(Pkg1), eqPkgRenderContext(pkg1Context))
    doWrite(
      """package pkg2;
        |
        |public class Class2 {
        |  public int method2(final int x) {
        |    return x + 4;
        |  }
        |}
        |""".stripMargin)
      .when(defaultStatRenderer).render(eqTree(Pkg2), eqPkgRenderContext(pkg2Context))

    sourceRenderer.render(TheSource, sourceContext)

    outputWriter.toString shouldBe
      """package pkg1;
        |
        |public class Class1 {
        |  public int method1(final int x) {
        |    return x + 2;
        |  }
        |}
        |package pkg2;
        |
        |public class Class2 {
        |  public int method2(final int x) {
        |    return x + 4;
        |  }
        |}
        |""".stripMargin
  }
}
