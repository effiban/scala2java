package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{JavaTreeTypeContext, ModifiersContext, ModifiersRenderContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.ModListTraversalResultMockitoMatcher.eqModListTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TypeParamListRenderer}
import io.github.effiban.scala2java.core.resolvers.JavaTreeTypeResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.Type.Bounds
import scala.meta.{Decl, Init, Mod, Name, Type, XtensionQuasiquoteTypeParam}

class DeclTypeTraverserImplTest extends UnitTestSuite {

  private val ScalaMods: List[Mod] = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val TraversedTypeParam1 = tparam"T11"
  private val TraversedTypeParam2 = tparam"T22"
  private val TraversedTypeParams = List(TraversedTypeParam1, TraversedTypeParam2)

  private val modListTraverser = mock[ModListTraverser]
  private val modifiersRenderContextFactory = mock[ModifiersRenderContextFactory]
  private val modListRenderer = mock[ModListRenderer]
  private val typeParamTraverser = mock[TypeParamTraverser]
  private val typeParamListRenderer = mock[TypeParamListRenderer]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]

  private val declTypeTraverser = new DeclTypeTraverserImpl(
    modListTraverser,
    modifiersRenderContextFactory,
    modListRenderer,
    typeParamTraverser,
    typeParamListRenderer,
    javaTreeTypeResolver)


  test("traverse()") {

    val declType = Decl.Type(
      mods = ScalaMods,
      name = Type.Name("MyType"),
      tparams = TypeParams,
      bounds = Bounds(lo = None, hi = Some(Type.Name("T")))
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Private))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Private))

    whenResolveJavaTreeTypeThenReturnInterface(declType)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(declType))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doAnswer((tparam: Type.Param) => tparam match {
      case aTypeParam if aTypeParam.structure == TypeParam1.structure => TraversedTypeParam1
      case aTypeParam if aTypeParam.structure == TypeParam2.structure => TraversedTypeParam2
      case aTypeParam => aTypeParam
    }).when(typeParamTraverser).traverse(any[Type.Param])
    doWrite("<T11, T22>").when(typeParamListRenderer).render(eqTreeList(TraversedTypeParams))

    declTypeTraverser.traverse(declType, StatContext(JavaScope.Class))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |private interface MyType<T11, T22> {
        |}
        |""".stripMargin
  }

  private def whenResolveJavaTreeTypeThenReturnInterface(declType: Decl.Type): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(declType, ScalaMods)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(JavaTreeType.Interface)
  }

  private def eqExpectedScalaMods(declType: Decl.Type) = {
    val expectedModifiersContext = ModifiersContext(declType, JavaTreeType.Interface, JavaScope.Class)
    eqModifiersContext(expectedModifiersContext)
  }
}
