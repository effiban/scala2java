package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.renderers.{PatListRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Decl, Init, Mod, Name, Type, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteType}

class DeclValTraverserImplTest extends UnitTestSuite {

  private val TheType = t"MyType"
  private val TheTraversedType = t"MyTraversedType"
  private val MyValPat = p"myVal"
  private val MyTraversedValPat = p"myTraversedVal"

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val Modifiers = List(TheAnnot)

  private val modListTraverser = mock[DeprecatedModListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val patTraverser = mock[PatTraverser]
  private val patListRenderer = mock[PatListRenderer]

  private val declValTraverser = new DeclValTraverserImpl(
    modListTraverser,
    typeTraverser,
    typeRenderer,
    patTraverser,
    patListRenderer
  )


  test("traverse() when it is a class member") {
    val javaScope = JavaScope.Class

    val declVal = Decl.Val(
      mods = Modifiers,
      pats = List(MyValPat),
      decltpe = TheType
    )

    doWrite(
      """@MyAnnotation
        |private final """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declVal, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TheTraversedType))
    doReturn(MyTraversedValPat).when(patTraverser).traverse(eqTree(MyValPat))
    doWrite("myTraversedVal").when(patListRenderer).render(eqTreeList(List(MyTraversedValPat)))

    declValTraverser.traverse(declVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final MyTraversedType myTraversedVal""".stripMargin
  }

  test("traverse() when it is an interface member") {
    val javaScope = JavaScope.Interface

    val modifiers = List(TheAnnot)

    val declVal = Decl.Val(
      mods = modifiers,
      pats = List(MyValPat),
      decltpe = TheType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declVal, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TheTraversedType))
    doReturn(MyTraversedValPat).when(patTraverser).traverse(eqTree(MyValPat))
    doWrite("myTraversedVal").when(patListRenderer).render(eqTreeList(List(MyTraversedValPat)))

    declValTraverser.traverse(declVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |MyTraversedType myTraversedVal""".stripMargin
  }

  private def eqExpectedModifiers(declVal: Decl.Val, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(declVal, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
