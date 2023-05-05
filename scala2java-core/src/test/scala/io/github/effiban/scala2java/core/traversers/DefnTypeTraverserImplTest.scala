package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{JavaTreeTypeContext, ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.renderers.{TypeBoundsRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.resolvers.JavaTreeTypeResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.Type.Bounds
import scala.meta.{Defn, Init, Mod, Name, Type, XtensionQuasiquoteType}

class DefnTypeTraverserImplTest extends UnitTestSuite {

  private val Modifiers: List[Mod.Annot] = List(
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
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val typeBoundsTraverser = mock[TypeBoundsTraverser]
  private val typeBoundsRenderer = mock[TypeBoundsRenderer]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]

  private val defnTypeTraverser = new DefnTypeTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    typeTraverser,
    typeRenderer,
    typeBoundsTraverser,
    typeBoundsRenderer,
    javaTreeTypeResolver)


  test("traverse() when has body and no bounds") {
    val javaScope = JavaScope.Class

    val defnType = Defn.Type(
      mods = Modifiers,
      name = MyType,
      tparams = TypeParams,
      body = MyOtherType
    )

    whenResolveJavaTreeTypeThenReturnInterface(defnType)
    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnType), annotsOnSameLine = ArgumentMatchers.eq(false))
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
      mods = Modifiers,
      name = MyType,
      tparams = TypeParams,
      body = Type.AnonymousName(),
      bounds = bounds
    )

    whenResolveJavaTreeTypeThenReturnInterface(defnType)
    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnType), annotsOnSameLine = ArgumentMatchers.eq(false))
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
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(defnType, Modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(JavaTreeType.Interface)
  }

  private def eqExpectedModifiers(defnType: Defn.Type) = {
    val expectedModifiersContext = ModifiersContext(defnType, JavaTreeType.Interface, JavaScope.Class)
    eqModifiersContext(expectedModifiersContext)
  }
}
