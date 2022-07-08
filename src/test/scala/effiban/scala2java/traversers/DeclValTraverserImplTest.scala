package effiban.scala2java.traversers

import effiban.scala2java.TraversalContext.javaOwnerContext
import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.{Class, Interface, JavaModifiersResolver, Method, UnitTestSuite}
import org.mockito.ArgumentMatchers

import scala.meta.Mod.Final
import scala.meta.{Decl, Init, Mod, Name, Pat, Term, Type}

class DeclValTraverserImplTest extends UnitTestSuite {

  private val JavaPrivateFinalModifiers = List("private", "final")
  private val JavaFinalModifiers = List("final")
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
    javaOwnerContext = Class

    val initialModifiers: List[Mod] = List(TheAnnot)
    val adjustedModifiers = initialModifiers :+ Final()

    val declVal = Decl.Val(
      mods = initialModifiers,
      pats = List(MyValPat),
      decltpe = IntType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(initialModifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolveForClassDataMember(eqTreeList(adjustedModifiers))).thenReturn(JavaPrivateFinalModifiers)
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))

    declValTraverser.traverse(declVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final int myVal""".stripMargin
  }

  test("traverse() when it is an interface member") {
    javaOwnerContext = Interface

    val modifiers: List[Mod] = List(TheAnnot)

    val declVal = Decl.Val(
      mods = modifiers,
      pats = List(MyValPat),
      decltpe = IntType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))

    declValTraverser.traverse(declVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVal""".stripMargin
  }

  test("traverse() when it is a local variable") {
    javaOwnerContext = Method

    val initialModifiers: List[Mod] = List(TheAnnot)
    val adjustedModifiers = initialModifiers :+ Final()

    val declVal = Decl.Val(
      mods = initialModifiers,
      pats = List(MyValPat),
      decltpe = IntType
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(initialModifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolve(eqTreeList(adjustedModifiers), ArgumentMatchers.eq(List(classOf[Final])))).thenReturn(JavaFinalModifiers)
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))

    declValTraverser.traverse(declVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |final int myVal""".stripMargin
  }
}
