package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.renderers.PatListRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Defn, Init, Lit, Mod, Name, Type, XtensionQuasiquoteCaseOrPattern}

class DeprecatedDefnVarTraverserImplTest extends UnitTestSuite {

  private val Modifiers = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )
  private val IntType = TypeNames.Int
  private val MyVarPat = p"myVar"
  private val MyTraversedVarPat = p"myTraversedVar"
  private val Rhs = Lit.Int(3)

  private val modListTraverser = mock[DeprecatedModListTraverser]
  private val defnValOrVarTypeTraverser = mock[DeprecatedDefnValOrVarTypeTraverser]
  private val patTraverser = mock[PatTraverser]
  private val patListRenderer = mock[PatListRenderer]
  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]

  private val defnVarTraverser = new DeprecatedDefnVarTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patTraverser,
    patListRenderer,
    expressionTermTraverser)


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
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myTraversedVar = 3""".stripMargin
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
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myTraversedVar""".stripMargin
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
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private int myTraversedVar = 3""".stripMargin
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
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myTraversedVar = 3""".stripMargin
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
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myTraversedVar""".stripMargin
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
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myTraversedVar = 3""".stripMargin
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
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myTraversedVar = 3""".stripMargin
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
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myTraversedVar""".stripMargin
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
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))
    doWrite("myTraversedVar").when(patListRenderer).render(eqTreeList(List(MyTraversedVarPat)))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Rhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |var myTraversedVar = 3""".stripMargin
  }

  private def eqExpectedModifiers(defnVar: Defn.Var, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(defnVar, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
