package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Decl, Init, Mod, Name, Pat, Term, Type, XtensionQuasiquoteType}

class DeclValTraverserImplTest extends UnitTestSuite {

  private val TheType = t"Foo"
  private val TheTraversedType = t"Bar"
  private val MyValPat = Pat.Var(Term.Name("myVal"))

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val Modifiers = List(TheAnnot)

  private val modListTraverser = mock[ModListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val patListTraverser = mock[PatListTraverser]

  private val declValTraverser = new DeclValTraverserImpl(
    modListTraverser,
    typeTraverser,
    typeRenderer,
    patListTraverser)


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
    doWrite("Bar").when(typeRenderer).render(eqTree(TheTraversedType))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))

    declValTraverser.traverse(declVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final Bar myVal""".stripMargin
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
    doWrite("Bar").when(typeRenderer).render(eqTree(TheTraversedType))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))

    declValTraverser.traverse(declVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |Bar myVal""".stripMargin
  }

  private def eqExpectedModifiers(declVal: Decl.Val, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(declVal, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
