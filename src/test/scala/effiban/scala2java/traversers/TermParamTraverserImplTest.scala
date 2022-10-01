package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.{Init, Lit, Mod, Name, Term, Type}

class TermParamTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.MethodSignature)

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
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

  test("traverse with type and default") {
    val mods = List(TheAnnot)

    val termParam = Term.Param(
      mods = List(TheAnnot),
      name = ParamName,
      decltpe = Some(TypeNames.Int),
      default = Some(Lit.Int(3))
    )

    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(mods = eqTreeList(mods), onSameLine = ArgumentMatchers.eq(true))
    whenResolveJavaModifiers(termParam, mods).thenReturn(List(JavaModifier.Final))
    doWrite("int").when(typeTraverser).traverse(eqTree(TypeNames.Int))
    doWrite("myParam").when(nameTraverser).traverse(eqTree(ParamName))

    termParamTraverser.traverse(termParam, TheStatContext)

    outputWriter.toString shouldBe "@MyAnnotation final int myParam/* = 3 */"
  }

  test("traverse without type and without default") {
    val mods = List(TheAnnot)

    val termParam = Term.Param(
      mods = List(TheAnnot),
      name = ParamName,
      decltpe = None,
      default = None
    )

    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(mods = eqTreeList(mods), onSameLine = ArgumentMatchers.eq(true))
    whenResolveJavaModifiers(termParam, mods).thenReturn(Nil)
    doWrite("myParam").when(nameTraverser).traverse(eqTree(ParamName))

    termParamTraverser.traverse(termParam, TheStatContext)

    outputWriter.toString shouldBe "@MyAnnotation myParam"
  }

  private def whenResolveJavaModifiers(termParam: Term.Param, modifiers: List[Mod]) = {
    val expectedContext = JavaModifiersContext(termParam, modifiers, JavaTreeType.Parameter, JavaScope.MethodSignature)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedContext)))
  }
}
