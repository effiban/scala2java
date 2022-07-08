package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.{Interface, Method}
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.{Defn, Init, Lit, Mod, Name, Pat, Term, Type}

class DefnVarTraverserImplTest extends UnitTestSuite {

  private val JavaPrivateModifiers = List("private")
  private val Modifiers = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )
  private val IntType = TypeNames.Int
  private val MyVarPat = Pat.Var(Term.Name("myVar"))
  private val Rhs = Lit.Int(3)

  private val annotListTraverser = mock[AnnotListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val patListTraverser = mock[PatListTraverser]
  private val termTraverser = mock[TermTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val defnVarTraverser = new DefnVarTraverserImpl(
    annotListTraverser,
    typeTraverser,
    patListTraverser,
    termTraverser,
    javaModifiersResolver)


  test("traverse() when it is a class member - typed with value") {
    javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolveForClassDataMember(eqTreeList(Modifiers))).thenReturn(JavaPrivateModifiers)
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar = 3""".stripMargin
  }

  test("traverse() when it is a class member - typed without value") {
    javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = None
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolveForClassDataMember(eqTreeList(Modifiers))).thenReturn(JavaPrivateModifiers)
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar""".stripMargin
  }

  test("traverse() when it is a class member - untyped with value") {
    javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolveForClassDataMember(eqTreeList(Modifiers))).thenReturn(JavaPrivateModifiers)
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private /* UnknownType */ myVar = 3""".stripMargin
  }

  test("traverse() when it is an interface member - typed with value") {
    javaScope = Interface

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar = 3""".stripMargin
  }

  test("traverse() when it is an interface member - typed without value") {
    javaScope = Interface

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = None
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  test("traverse() when it is an interface member - untyped with value") {
    javaScope = Interface

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |/* UnknownType */ myVar = 3""".stripMargin
  }

  test("traverse() when it is a local variable - typed with value") {
    javaScope = Method

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar = 3""".stripMargin
  }

  test("traverse() when it is a local variable - typed without value") {
    javaScope = Method

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = None
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  test("traverse() when it is a local variable - untyped with value") {
    javaScope = Method

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(mods = eqTreeList(Modifiers), onSameLine = ArgumentMatchers.eq(false))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(termTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |var myVar = 3""".stripMargin
  }
}
