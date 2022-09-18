package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.{Decl, Init, Mod, Name, Pat, Term, Type}

class DeclVarTraverserImplTest extends UnitTestSuite {

  private val JavaPrivateModifiers = List(JavaModifier.Private)
  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val IntType = TypeNames.Int
  private val MyVarPat = Pat.Var(Term.Name("myVar"))

  private val annotListTraverser = mock[AnnotListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val patListTraverser = mock[PatListTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val declVarTraverser = new DeclVarTraverserImpl(
    annotListTraverser,
    typeTraverser,
    patListTraverser,
    javaModifiersResolver)


  test("traverse() when it is a class member") {
    val javaScope = JavaScope.Class

    val modifiers = List(TheAnnot)

    val declVar = Decl.Var(
      mods = modifiers,
      pats = List(MyVarPat),
      decltpe = IntType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(declVar, modifiers, javaScope).thenReturn(JavaPrivateModifiers)
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))

    declVarTraverser.traverse(declVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar""".stripMargin
  }

  test("traverse() when it is an interface member") {
    val javaScope = JavaScope.Interface

    val modifiers: List[Mod] = List(TheAnnot)

    val declVar = Decl.Var(
      mods = modifiers,
      pats = List(MyVarPat),
      decltpe = IntType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(declVar, modifiers, javaScope).thenReturn(List.empty)
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))

    declVarTraverser.traverse(declVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  test("traverse() when it is a local variable") {
    val javaScope = JavaScope.Block

    val modifiers: List[Mod] = List(TheAnnot)

    val declVar = Decl.Var(
      mods = modifiers,
      pats = List(MyVarPat),
      decltpe = IntType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    whenResolveJavaModifiers(declVar, modifiers, javaScope).thenReturn(List.empty)
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))

    declVarTraverser.traverse(declVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  private def whenResolveJavaModifiers(declVar: Decl.Var, modifiers: List[Mod], javaScope: JavaScope) = {
    val expectedJavaModifiersContext = JavaModifiersContext(declVar, modifiers, JavaTreeType.Variable, javaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedJavaModifiersContext)))
  }
}
