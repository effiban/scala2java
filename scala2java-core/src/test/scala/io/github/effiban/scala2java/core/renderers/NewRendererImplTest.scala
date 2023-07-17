package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.matchers.ArrayInitializerSizeRenderContextMockitoMatcher.eqArrayInitializerSizeRenderContext
import io.github.effiban.scala2java.core.renderers.contexts.{ArrayInitializerSizeRenderContext, InitRenderContext}
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerRenderContextResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.Term.New
import scala.meta.{Init, Lit, Name, Term, Type}

class NewRendererImplTest extends UnitTestSuite {

  private val initRenderer = mock[InitRenderer]
  private val arrayInitializerRenderer = mock[ArrayInitializerRenderer]
  private val arrayInitializerRenderContextResolver = mock[ArrayInitializerRenderContextResolver]

  private val newRenderer = new NewRendererImpl(
    initRenderer,
    arrayInitializerRenderer,
    arrayInitializerRenderContextResolver
  )


  test("render instantiation of 'MyClass'") {
    val init = Init(
      tpe = Type.Name("MyClass"),
      name = Name.Anonymous(),
      argss = List(List(Term.Name("val1"), Term.Name("val2")))
    )

    val `new` = New(init)

    when(arrayInitializerRenderContextResolver.tryResolve(`new`.init)).thenReturn(None)
    doWrite("MyClass(val1, val2)")
      .when(initRenderer).render(eqTree(init), ArgumentMatchers.eq(InitRenderContext(renderEmpty = true, argNameAsComment = true)))

    newRenderer.render(`new`)

    outputWriter.toString shouldBe "new MyClass(val1, val2)"
  }

  test("render instantiation of 'Array'") {
    val init = Init(
      tpe = Type.Apply(TypeNames.ScalaArray, List(TypeNames.String)),
      name = Name.Anonymous(),
      argss = List(List(Lit.Int(3)))
    )
    val `new` = New(init)

    val expectedContext = ArrayInitializerSizeRenderContext(tpe = TypeNames.String, size = Lit.Int(3))

    when(arrayInitializerRenderContextResolver.tryResolve(`new`.init)).thenReturn(Some(expectedContext))

    newRenderer.render(`new`)

    verify(arrayInitializerRenderer).renderWithSize(eqArrayInitializerSizeRenderContext(expectedContext))
  }
}
