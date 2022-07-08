package effiban.scala2java

import effiban.scala2java.TraversalContext.javaOwnerContext
import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.Mod.Final
import scala.meta.{Init, Mod, Name, Term, Type}

class TermParamTraverserImplTest extends UnitTestSuite {

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val JavaFinalModifiers = List("final")
  private val ParamName = Name.Indeterminate("myParam")

  private val annotListTraverser = mock[AnnotListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val nameTraverser = mock[NameTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val termParamTraverser = new TermParamTraverserImpl(
    annotListTraverser,
    typeTraverser,
    nameTraverser,
    javaModifiersResolver
  )

  test("traverse for regular method") {
    javaOwnerContext = Method

    val initialMods = List(TheAnnot)
    val adjustedMods = List(TheAnnot) :+ Final()

    val termParam = Term.Param(
      mods = List(TheAnnot),
      name = ParamName,
      decltpe = Some(TypeNames.Int),
      default = None
    )

    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(mods = eqTreeList(initialMods), onSameLine = ArgumentMatchers.eq(true))
    when(javaModifiersResolver.resolve(eqTreeList(adjustedMods), ArgumentMatchers.eq(List(classOf[Final]))))
      .thenReturn(JavaFinalModifiers)
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myParam").when(nameTraverser).traverse(eqTree(ParamName))

    termParamTraverser.traverse(termParam)

    outputWriter.toString shouldBe "@MyAnnotation final int myParam"
  }

  test("traverse for lambda with type") {
    javaOwnerContext = Lambda

    val mods = List(TheAnnot)

    val termParam = Term.Param(
      mods = List(TheAnnot),
      name = ParamName,
      decltpe = Some(TypeNames.Int),
      default = None
    )

    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(mods = eqTreeList(mods), onSameLine = ArgumentMatchers.eq(true))
    when(javaModifiersResolver.resolve(eqTreeList(mods), ArgumentMatchers.eq(List(classOf[Final])))).thenReturn(Nil)
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myParam").when(nameTraverser).traverse(eqTree(ParamName))

    termParamTraverser.traverse(termParam)

    outputWriter.toString shouldBe "@MyAnnotation int myParam"
  }

  test("traverse for lambda without type") {
    javaOwnerContext = Lambda

    val mods = List(TheAnnot)

    val termParam = Term.Param(
      mods = List(TheAnnot),
      name = ParamName,
      decltpe = None,
      default = None
    )

    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(mods = eqTreeList(mods), onSameLine = ArgumentMatchers.eq(true))
    when(javaModifiersResolver.resolve(eqTreeList(mods), ArgumentMatchers.eq(List(classOf[Final])))).thenReturn(Nil)
    doWrite("myParam").when(nameTraverser).traverse(eqTree(ParamName))

    termParamTraverser.traverse(termParam)

    outputWriter.toString shouldBe "@MyAnnotation myParam"
  }
}
