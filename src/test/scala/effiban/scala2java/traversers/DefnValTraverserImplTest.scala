package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.{Interface, Method}
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.Mod.Final
import scala.meta.{Defn, Init, Lit, Mod, Name, Pat, Term, Type}

class DefnValTraverserImplTest extends UnitTestSuite {

  private val JavaPrivateFinalModifiers = List("private", "final")
  private val JavaFinalModifiers = List("final")
  private val IntType = TypeNames.Int
  private val MyValPat = Pat.Var(Term.Name("myVal"))
  private val Rhs = Lit.Int(3)

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )

  private val annotListTraverser = mock[AnnotListTraverser]
  private val defnValOrVarTypeTraverser = mock[DefnValOrVarTypeTraverser]
  private val patListTraverser = mock[PatListTraverser]
  private val termTraverser = mock[TermTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val defnValTraverser = new DefnValTraverserImpl(
    annotListTraverser,
    defnValOrVarTypeTraverser,
    patListTraverser,
    termTraverser,
    javaModifiersResolver)


  test("traverse() when it is a class member - typed") {
    javaScope = JavaScope.Class

    val initialModifiers: List[Mod] = List(TheAnnot)
    val adjustedModifiers = initialModifiers :+ Final()

    val defnVal = Defn.Val(
      mods = initialModifiers,
      pats = List(MyValPat),
      decltpe = Some(TypeNames.Int),
      rhs = Rhs
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(initialModifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolveForClassDataMember(eqTreeList(adjustedModifiers))).thenReturn(JavaPrivateFinalModifiers)
    doWrite("int").when(defnValOrVarTypeTraverser).traverse(eqSomeTree(IntType), eqSomeTree(Rhs))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final int myVal = 3""".stripMargin
  }

  test("traverse() when it is a class member - untyped") {
    javaScope = JavaScope.Class

    val initialModifiers: List[Mod] = List(TheAnnot)
    val adjustedModifiers = initialModifiers :+ Final()

    val defnVal = Defn.Val(
      mods = initialModifiers,
      pats = List(MyValPat),
      decltpe = None,
      rhs = Rhs
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(initialModifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolveForClassDataMember(eqTreeList(adjustedModifiers))).thenReturn(JavaPrivateFinalModifiers)
    doWrite("int").when(defnValOrVarTypeTraverser).traverse(ArgumentMatchers.eq(None), eqSomeTree(Rhs))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final int myVal = 3""".stripMargin
  }

  test("traverse() when it is an interface member - typed") {
    javaScope = Interface

    val modifiers: List[Mod] = List(TheAnnot)

    val defnVal = Defn.Val(
      mods = modifiers,
      pats = List(MyValPat),
      decltpe = Some(TypeNames.Int),
      rhs = Rhs
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(defnValOrVarTypeTraverser).traverse(eqSomeTree(IntType), eqSomeTree(Rhs))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVal = 3""".stripMargin
  }

  test("traverse() when it is an interface member - untyped") {
    javaScope = Interface

    val modifiers: List[Mod] = List(TheAnnot)

    val defnVal = Defn.Val(
      mods = modifiers,
      pats = List(MyValPat),
      decltpe = None,
      rhs = Rhs
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(modifiers), onSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(defnValOrVarTypeTraverser).traverse(ArgumentMatchers.eq(None), eqSomeTree(Rhs))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVal = 3""".stripMargin
  }

  test("traverse() when it is a local variable - typed") {
    javaScope = Method

    val initialModifiers: List[Mod] = List(TheAnnot)
    val adjustedModifiers = initialModifiers :+ Final()

    val defnVal = Defn.Val(
      mods = initialModifiers,
      pats = List(MyValPat),
      decltpe = Some(TypeNames.Int),
      rhs = Rhs
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(initialModifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolve(inputScalaMods = eqTreeList(adjustedModifiers), allowedJavaModifiers = ArgumentMatchers.eq(List("final"))))
      .thenReturn(JavaFinalModifiers)
    doWrite("int").when(defnValOrVarTypeTraverser).traverse(eqSomeTree(IntType), eqSomeTree(Rhs))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |final int myVal = 3""".stripMargin
  }

  test("traverse() when it is a local variable - untyped") {
    javaScope = Method

    val initialModifiers: List[Mod] = List(TheAnnot)
    val adjustedModifiers = initialModifiers :+ Final()

    val defnVal = Defn.Val(
      mods = initialModifiers,
      pats = List(MyValPat),
      decltpe = None,
      rhs = Rhs
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(initialModifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolve(inputScalaMods = eqTreeList(adjustedModifiers), allowedJavaModifiers = ArgumentMatchers.eq(List("final"))))
      .thenReturn(JavaFinalModifiers)
    doWrite("var").when(defnValOrVarTypeTraverser).traverse(ArgumentMatchers.eq(None), eqSomeTree(Rhs))
    doWrite("myVal").when(patListTraverser).traverse(eqTreeList(List(MyValPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnValTraverser.traverse(defnVal)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |final var myVal = 3""".stripMargin
  }
}
