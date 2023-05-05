package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.renderers.{NameRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Init, Lit, Mod, Name, Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermParamTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.MethodSignature)

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val Modifiers = List(TheAnnot)
  private val ParamName = q"myParam"
  private val TraversedParamName = q"traversedMyParam"

  private val modListTraverser = mock[ModListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val nameTraverser = mock[NameTraverser]
  private val nameRenderer = mock[NameRenderer]

  private val termParamTraverser = new TermParamTraverserImpl(
    modListTraverser,
    typeTraverser,
    typeRenderer,
    nameTraverser,
    nameRenderer
  )

  test("traverse with type and default") {
    val termParam = Term.Param(
      mods = Modifiers,
      name = ParamName,
      decltpe = Some(TypeNames.Int),
      default = Some(Lit.Int(3))
    )

    doWrite("@MyAnnotation final ")
      .when(modListTraverser).traverse(eqExpectedModifiers(termParam), annotsOnSameLine = ArgumentMatchers.eq(true))
    doReturn(t"int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("int").when(typeRenderer).render(eqTree(t"int"))
    doReturn(TraversedParamName).when(nameTraverser).traverse(eqTree(ParamName))
    doWrite("myTraversedParam").when(nameRenderer).render(eqTree(TraversedParamName))

    termParamTraverser.traverse(termParam, TheStatContext)

    outputWriter.toString shouldBe "@MyAnnotation final int myTraversedParam/* = 3 */"
  }

  test("traverse without type and without default") {
    val termParam = Term.Param(
      mods = Modifiers,
      name = ParamName,
      decltpe = None,
      default = None
    )

    doWrite("@MyAnnotation ")
      .when(modListTraverser).traverse(eqExpectedModifiers(termParam), annotsOnSameLine = ArgumentMatchers.eq(true))
    doReturn(TraversedParamName).when(nameTraverser).traverse(eqTree(ParamName))
    doWrite("myTraversedParam").when(nameRenderer).render(eqTree(TraversedParamName))

    termParamTraverser.traverse(termParam, TheStatContext)

    outputWriter.toString shouldBe "@MyAnnotation myTraversedParam"
  }

  private def eqExpectedModifiers(termParam: Term.Param) = {
    val expectedModifiersContext = ModifiersContext(termParam, JavaTreeType.Parameter, JavaScope.MethodSignature)
    eqModifiersContext(expectedModifiersContext)
  }
}
