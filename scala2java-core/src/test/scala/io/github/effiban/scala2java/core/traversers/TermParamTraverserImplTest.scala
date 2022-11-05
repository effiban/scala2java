package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.{Init, Lit, Mod, Name, Term, Type}

class TermParamTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.MethodSignature)

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val Modifiers = List(TheAnnot)
  private val ParamName = Name.Indeterminate("myParam")

  private val modListTraverser = mock[ModListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val nameTraverser = mock[NameTraverser]

  private val termParamTraverser = new TermParamTraverserImpl(
    modListTraverser,
    typeTraverser,
    nameTraverser
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
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myParam").when(nameTraverser).traverse(eqTree(ParamName))

    termParamTraverser.traverse(termParam, TheStatContext)

    outputWriter.toString shouldBe "@MyAnnotation final int myParam/* = 3 */"
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
    doWrite("myParam").when(nameTraverser).traverse(eqTree(ParamName))

    termParamTraverser.traverse(termParam, TheStatContext)

    outputWriter.toString shouldBe "@MyAnnotation myParam"
  }

  private def eqExpectedModifiers(termParam: Term.Param) = {
    val expectedModifiersContext = ModifiersContext(termParam, JavaTreeType.Parameter, JavaScope.MethodSignature)
    eqModifiersContext(expectedModifiersContext)
  }
}
