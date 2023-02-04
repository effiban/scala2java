package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Defn, Init, Lit, Mod, Name, Pat, Term, Type}

class DefnVarTraverserImplTest extends UnitTestSuite {

  private val Modifiers = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )
  private val IntType = TypeNames.Int
  private val MyVarPat = Pat.Var(Term.Name("myVar"))
  private val Rhs = Lit.Int(3)

  private val modListTraverser = mock[ModListTraverser]
  private val defnValOrVarTypeTraverser = mock[DefnValOrVarTypeTraverser]
  private val patListTraverser = mock[PatListTraverser]
  private val expressionTraverser = mock[ExpressionTraverser]

  private val defnVarTraverser = new DefnVarTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patListTraverser,
    expressionTraverser)


  test("traverse() when it is a class member - typed with value") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(expressionTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar = 3""".stripMargin
  }

  test("traverse() when it is a class member - typed without value") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = None
    )

    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
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
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      ArgumentMatchers.eq(None),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(expressionTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myVar = 3""".stripMargin
  }

  test("traverse() when it is an interface member - typed with value") {
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(expressionTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar = 3""".stripMargin
  }

  test("traverse() when it is an interface member - typed without value") {
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = None
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
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
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(
      ArgumentMatchers.eq(None),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(expressionTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar = 3""".stripMargin
  }

  test("traverse() when it is a local variable - typed with value") {
    val javaScope = JavaScope.Block

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(
      defnValOrVarTypeTraverser).traverse(
      eqSomeTree(IntType),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(expressionTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar = 3""".stripMargin
  }

  test("traverse() when it is a local variable - typed without value") {
    val javaScope = JavaScope.Block

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = Some(TypeNames.Int),
      rhs = None
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
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
    val javaScope = JavaScope.Block

    val defnVar = Defn.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(Rhs)
    )

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("var")
      .when(defnValOrVarTypeTraverser).traverse(
      ArgumentMatchers.eq(None),
      eqSomeTree(Rhs),
      ArgumentMatchers.eq(StatContext(javaScope))
    )
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))
    doWrite("3").when(expressionTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |var myVar = 3""".stripMargin
  }

  private def eqExpectedModifiers(defnVar: Defn.Var, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(defnVar, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
