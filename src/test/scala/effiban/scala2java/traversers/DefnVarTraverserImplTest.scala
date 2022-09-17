package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import effiban.scala2java.entities.JavaTreeType.{Interface, JavaTreeType, Method}
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.{Defn, Init, Lit, Mod, Name, Pat, Term, Type}

class DefnVarTraverserImplTest extends UnitTestSuite {

  private val JavaPrivateModifiers = List(JavaModifier.Private)
  private val Modifiers = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )
  private val IntType = TypeNames.Int
  private val MyVarPat = Pat.Var(Term.Name("myVar"))
  private val Rhs = Lit.Int(3)

  private val annotListTraverser = mock[AnnotListTraverser]
  private val defnValOrVarTypeTraverser = mock[DefnValOrVarTypeTraverser]
  private val patListTraverser = mock[PatListTraverser]
  private val rhsTermTraverser = mock[RhsTermTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val defnVarTraverser = new DefnVarTraverserImpl(
    annotListTraverser,
    defnValOrVarTypeTraverser,
    patListTraverser,
    rhsTermTraverser,
    javaModifiersResolver)


  test("traverse() when it is a class member - typed with value") {
    val javaScope = JavaTreeType.Class

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
    whenResolveJavaModifiers(defnVar, javaScope).thenReturn(JavaPrivateModifiers)
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar = 3""".stripMargin
  }

  test("traverse() when it is a class member - typed without value") {
    val javaScope = JavaTreeType.Class

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
    whenResolveJavaModifiers(defnVar, javaScope).thenReturn(JavaPrivateModifiers)
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      ArgumentMatchers.eq(None),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar""".stripMargin
  }

  test("traverse() when it is a class member - untyped with value") {
    val javaScope = JavaTreeType.Class

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
    whenResolveJavaModifiers(defnVar, javaScope).thenReturn(JavaPrivateModifiers)
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      ArgumentMatchers.eq(None),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar = 3""".stripMargin
  }

  test("traverse() when it is an interface member - typed with value") {
    val javaScope = Interface

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
    whenResolveJavaModifiers(defnVar, javaScope).thenReturn(Nil)
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar = 3""".stripMargin
  }

  test("traverse() when it is an interface member - typed without value") {
    val javaScope = Interface

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
    whenResolveJavaModifiers(defnVar, javaScope).thenReturn(Nil)
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      ArgumentMatchers.eq(None),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  test("traverse() when it is an interface member - untyped with value") {
    val javaScope = Interface

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
    whenResolveJavaModifiers(defnVar, javaScope).thenReturn(Nil)
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      ArgumentMatchers.eq(None),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar = 3""".stripMargin
  }

  test("traverse() when it is a local variable - typed with value") {
    val javaScope = Method

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
    whenResolveJavaModifiers(defnVar, javaScope).thenReturn(Nil)
    doWrite("int").when(
      defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar = 3""".stripMargin
  }

  test("traverse() when it is a local variable - typed without value") {
    val javaScope = Method

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
    whenResolveJavaModifiers(defnVar, javaScope).thenReturn(Nil)
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      ArgumentMatchers.eq(None),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  test("traverse() when it is a local variable - untyped with value") {
    val javaScope = Method

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
    whenResolveJavaModifiers(defnVar, javaScope).thenReturn(Nil)
    doWrite("var")
      .when(defnValOrVarTypeTraverser).traverse(
      ArgumentMatchers.eq(None),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |var myVar = 3""".stripMargin
  }

  private def whenResolveJavaModifiers(defnVar: Defn.Var, javaScope: JavaTreeType) = {
    val expectedJavaModifiersContext = JavaModifiersContext(defnVar, Modifiers, JavaTreeType.Variable, javaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedJavaModifiersContext)))
  }
}
