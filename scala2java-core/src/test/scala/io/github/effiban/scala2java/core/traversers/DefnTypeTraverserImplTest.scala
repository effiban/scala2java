package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{JavaTreeTypeContext, ModifiersContext, ModifiersRenderContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.ModListTraversalResultMockitoMatcher.eqModListTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TypeBoundsRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.resolvers.JavaTreeTypeResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Type.Bounds
import scala.meta.{Defn, Init, Mod, Name, Type, XtensionQuasiquoteType}

class DefnTypeTraverserImplTest extends UnitTestSuite {

  private val ScalaMods: List[Mod.Annot] = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )

  private val TypeParams = List(
    Type.Param(
      mods = List(),
      name = Type.Name("T"),
      tparams = List(),
      tbounds = Bounds(lo = None, hi = None),
      vbounds = List(),
      cbounds = List()
    )
  )

  private val MyType = t"MyType"
  private val MyOtherType = t"MyOtherType"
  private val MyTraversedOtherType = t"MyTraversedOtherType"

  private val modListTraverser = mock[ModListTraverser]
  private val modifiersRenderContextFactory = mock[ModifiersRenderContextFactory]
  private val modListRenderer = mock[ModListRenderer]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val typeBoundsTraverser = mock[TypeBoundsTraverser]
  private val typeBoundsRenderer = mock[TypeBoundsRenderer]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]

  private val defnTypeTraverser = new DefnTypeTraverserImpl(
    modListTraverser,
    modifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    typeTraverser,
    typeRenderer,
    typeBoundsTraverser,
    typeBoundsRenderer,
    javaTreeTypeResolver)


  test("traverse() when has body and no bounds") {
    val javaScope = JavaScope.Class

    val defnType = Defn.Type(
      mods = ScalaMods,
      name = MyType,
      tparams = TypeParams,
      body = MyOtherType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Private))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Private))

    whenResolveJavaTreeTypeThenReturnInterface(defnType)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(defnType))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doReturn(MyTraversedOtherType).when(typeTraverser).traverse(eqTree(MyOtherType))
    doWrite("MyTraversedOtherType").when(typeRenderer).render(eqTree(MyTraversedOtherType))

    defnTypeTraverser.traverse(defnType, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private interface MyType<T> extends MyTraversedOtherType {
        |}
        |""".stripMargin
  }

  test("traverse() when has no body and has upper bound") {
    val bounds = Bounds(lo = None, hi = Some(MyOtherType))
    val traversedBounds = Bounds(lo = None, hi = Some(MyTraversedOtherType))
    val defnType = Defn.Type(
      mods = ScalaMods,
      name = MyType,
      tparams = TypeParams,
      body = Type.AnonymousName(),
      bounds = bounds
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Private))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Private))

    whenResolveJavaTreeTypeThenReturnInterface(defnType)
    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(defnType))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doReturn(traversedBounds).when(typeBoundsTraverser).traverse(eqTree(bounds))
    doWrite("extends MyTraversedOtherType").when(typeBoundsRenderer).render(eqTree(traversedBounds))

    defnTypeTraverser.traverse(defnType, StatContext(JavaScope.Class))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private interface MyType<T> extends MyTraversedOtherType {
        |}
        |""".stripMargin
  }

  private def whenResolveJavaTreeTypeThenReturnInterface(defnType: Defn.Type): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(defnType, ScalaMods)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(JavaTreeType.Interface)
  }

  private def eqExpectedScalaMods(defnType: Defn.Type) = {
    val expectedModifiersContext = ModifiersContext(defnType, JavaTreeType.Interface, JavaScope.Class)
    eqModifiersContext(expectedModifiersContext)
  }
}
