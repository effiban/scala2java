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

class DeprecatedDeclVarTraverserImplTest extends UnitTestSuite {

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val Modifiers = List(TheAnnot)
  private val TheType = t"MyType"
  private val TheTraversedType = t"MyTraversedType"
  private val MyVarPat = p"myVar"
  private val MyTraversedVarPat = p"myTraversedVar"

  private val modListTraverser = mock[DeprecatedModListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val patTraverser = mock[PatTraverser]
  private val patListRenderer = mock[PatListRenderer]

  private val declVarTraverser = new DeprecatedDeclVarTraverserImpl(
    modListTraverser,
    typeTraverser,
    typeRenderer,
    patTraverser,
    patListRenderer
  )


  test("traverse() when it is a class member") {
    val javaScope = JavaScope.Class

    val declVar = Decl.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = TheType
    )

    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TheTraversedType))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))

    declVarTraverser.traverse(declVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private MyTraversedType myTraversedVar""".stripMargin
  }

  test("traverse() when it is an interface member") {
    val javaScope = JavaScope.Interface

    val modifiers: List[Mod] = List(TheAnnot)

    val declVar = Decl.Var(
      mods = modifiers,
      pats = List(MyVarPat),
      decltpe = TheType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TheTraversedType))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))

    declVarTraverser.traverse(declVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |MyTraversedType myTraversedVar""".stripMargin
  }

  test("traverse() when it is a local variable") {
    val javaScope = JavaScope.Block

    val modifiers: List[Mod] = List(TheAnnot)

    val declVar = Decl.Var(
      mods = modifiers,
      pats = List(MyVarPat),
      decltpe = TheType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TheTraversedType))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TheTraversedType))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))

    declVarTraverser.traverse(declVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |MyTraversedType myTraversedVar""".stripMargin
  }

  private def eqExpectedModifiers(declVar: Decl.Var, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(declVar, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
