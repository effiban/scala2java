package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType.{Lambda, Method}
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.{Init, Mod, Name, Term, Type}

class TermParamTraverserImplTest extends UnitTestSuite {

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val ParamName = Name.Indeterminate("myParam")

  private val annotListTraverser = mock[AnnotListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val nameTraverser = mock[NameTraverser]

  private val termParamTraverser = new TermParamTraverserImpl(
    annotListTraverser,
    typeTraverser,
    nameTraverser
  )

  test("traverse for regular method") {
    javaScope = Method

    val mods = List(TheAnnot)

    val termParam = Term.Param(
      mods = List(TheAnnot),
      name = ParamName,
      decltpe = Some(TypeNames.Int),
      default = None
    )

    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(mods = eqTreeList(mods), onSameLine = ArgumentMatchers.eq(true))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myParam").when(nameTraverser).traverse(eqTree(ParamName))

    termParamTraverser.traverse(termParam)

    outputWriter.toString shouldBe "@MyAnnotation final int myParam"
  }

  test("traverse for lambda with type") {
    javaScope = Lambda

    val mods = List(TheAnnot)

    val termParam = Term.Param(
      mods = List(TheAnnot),
      name = ParamName,
      decltpe = Some(TypeNames.Int),
      default = None
    )

    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(mods = eqTreeList(mods), onSameLine = ArgumentMatchers.eq(true))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myParam").when(nameTraverser).traverse(eqTree(ParamName))

    termParamTraverser.traverse(termParam)

    outputWriter.toString shouldBe "@MyAnnotation int myParam"
  }

  test("traverse for lambda without type") {
    javaScope = Lambda

    val mods = List(TheAnnot)

    val termParam = Term.Param(
      mods = List(TheAnnot),
      name = ParamName,
      decltpe = None,
      default = None
    )

    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(mods = eqTreeList(mods), onSameLine = ArgumentMatchers.eq(true))
    doWrite("myParam").when(nameTraverser).traverse(eqTree(ParamName))

    termParamTraverser.traverse(termParam)

    outputWriter.toString shouldBe "@MyAnnotation myParam"
  }
}
