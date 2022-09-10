package effiban.scala2java.traversers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaTreeType.Interface
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.{Decl, Init, Mod, Name, Pat, Term, Type}

class DeclValTraverserImplTest extends UnitTestSuite {

  private val JavaPrivateFinalModifiers = List(JavaModifier.Private, JavaModifier.Final)
  private val JavaFinalModifiers = List(JavaModifier.Final)
  private val IntType = TypeNames.Int
  private val MyValPat = Pat.Var(Term.Name("myVal"))

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )

  private val annotListTraverser = mock[AnnotListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val patListTraverser = mock[PatListTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val declValTraverser = new DeclValTraverserImpl(
    annotListTraverser,
    typeTraverser,
    patListTraverser,
    javaModifiersResolver)


  test("traverse() when it is a class member") {
    javaScope = JavaTreeType.Class

    val modifiers = List(TheAnnot)

    val declVal = Decl.Val(
      mods = modifiers,
      pats = List(MyValPat),
      decltpe = IntType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(declVal, modifiers).thenReturn(JavaPrivateFinalModifiers)
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))

    declValTraverser.traverse(declVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final int myVal""".stripMargin
  }

  test("traverse() when it is an interface member") {
    javaScope = Interface

    val modifiers = List(TheAnnot)

    val declVal = Decl.Val(
      mods = modifiers,
      pats = List(MyValPat),
      decltpe = IntType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(declVal, modifiers).thenReturn(List.empty)
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))

    declValTraverser.traverse(declVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVal""".stripMargin
  }

  private def whenResolveJavaModifiers(declVal: Decl.Val, modifiers: List[Mod]) = {
    val expectedContext = JavaModifiersContext(declVal, modifiers, JavaTreeType.Variable, javaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedContext)))
  }
}
