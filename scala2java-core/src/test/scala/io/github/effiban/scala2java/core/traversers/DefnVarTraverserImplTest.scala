package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.WithJavaModifiersTraversalResultScalatestMatcher.equalWithJavaModifiersTraversalResult
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{DefnVarTraversalResult, ModListTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnVarTransformer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqSomeTree
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Defn, Mod, Name, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnVarTraverserImplTest extends UnitTestSuite {

  private val TheAnnot = mod"@MyAnnotation"
  private val TheTransformedAnnot = mod"@MyTransformedAnnotation"
  private val TheTraversedAnnot = mod"@MyTraversedAnnotation"
  private val TheScalaMods = List(TheAnnot, Mod.Private(Name.Anonymous()))
  private val TheTransformedScalaMods = List(TheTransformedAnnot, Mod.Private(Name.Anonymous()))
  private val TheTraversedScalaMods = List(TheTraversedAnnot, Mod.Private(Name.Anonymous()))
  private val TheJavaModifiers = List(JavaModifier.Private)
  private val TheModListResult = ModListTraversalResult(TheTraversedScalaMods, TheJavaModifiers)

  private val MyVarPat = p"myVar"
  private val MyTransformedVarPat = p"myTransformedVar"
  private val MyTraversedVarPat = p"myTraversedVar"

  private val TheType = t"MyType"
  private val TheTransformedType = t"MyTransformedType"
  private val TheTraversedType = t"MyTraversedType"

  private val TheRhs = q"3"
  private val TheTransformedRhs = q"33"
  private val TheTraversedRhs = q"333"

  private val modListTraverser = mock[ModListTraverser]
  private val defnValOrVarTypeTraverser = mock[DefnValOrVarTypeTraverser]
  private val patTraverser = mock[PatTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val defnVarTransformer = mock[DefnVarTransformer]

  private val defnVarTraverser = new DefnVarTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patTraverser,
    expressionTermTraverser,
    defnVarTransformer
  )


  test("traverse() when it is a class member - typed with value") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = Some(TheType),
      rhs = Some(TheRhs)
    )
    val transformedDefnVar = Defn.Var(
      mods = TheTransformedScalaMods,
      pats = List(MyTransformedVarPat),
      decltpe = Some(TheTransformedType),
      rhs = Some(TheTransformedRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnVar, javaScope))
    doReturn(Some(TheTraversedType))
      .when(defnValOrVarTypeTraverser).traverse(eqSomeTree(TheTransformedType), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is a class member - typed without value") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = Some(TheType),
      rhs = None
    )
    val transformedDefnVar = Defn.Var(
      mods = TheTransformedScalaMods,
      pats = List(MyTransformedVarPat),
      decltpe = Some(TheTransformedType),
      rhs = None
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = None
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnVar, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqSomeTree(TheTransformedType), eqTo(None))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is a class member - untyped with value - type inferred") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(TheRhs)
    )
    val transformedDefnVar = Defn.Var(
      mods = TheTransformedScalaMods,
      pats = List(MyTransformedVarPat),
      decltpe = None,
      rhs = Some(TheTransformedRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnVar, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is a class member - untyped with value - type not inferred") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(TheRhs)
    )
    val transformedDefnVar = Defn.Var(
      mods = TheTransformedScalaMods,
      pats = List(MyTransformedVarPat),
      decltpe = None,
      rhs = Some(TheTransformedRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = None,
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnVar, javaScope))
    doReturn(None).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is an interface member - typed with value") {
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = Some(TheType),
      rhs = Some(TheRhs)
    )
    val transformedDefnVar = Defn.Var(
      mods = TheTransformedScalaMods,
      pats = List(MyTransformedVarPat),
      decltpe = Some(TheTransformedType),
      rhs = Some(TheTransformedRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnVar, javaScope))
    doReturn(Some(TheTraversedType))
      .when(defnValOrVarTypeTraverser).traverse(eqSomeTree(TheTransformedType), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is an interface member - typed without value") {
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = Some(TheType),
      rhs = None
    )
    val transformedDefnVar = Defn.Var(
      mods = TheTransformedScalaMods,
      pats = List(MyTransformedVarPat),
      decltpe = Some(TheTransformedType),
      rhs = None
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = None
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnVar, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqSomeTree(TheTransformedType), eqTo(None))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is an interface member - untyped with value - type inferred") {
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(TheRhs)
    )
    val transformedDefnVar = Defn.Var(
      mods = TheTransformedScalaMods,
      pats = List(MyTransformedVarPat),
      decltpe = None,
      rhs = Some(TheTransformedRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = Some(TheTraversedType),
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnVar, javaScope))
    doReturn(Some(TheTraversedType)).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  test("traverse() when it is an interface member - untyped with value - type not inferred") {
    val javaScope = JavaScope.Interface

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = None,
      rhs = Some(TheRhs)
    )
    val transformedDefnVar = Defn.Var(
      mods = TheTransformedScalaMods,
      pats = List(MyTransformedVarPat),
      decltpe = None,
      rhs = Some(TheTransformedRhs)
    )
    val traversedDefnVar = Defn.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = None,
      rhs = Some(TheTraversedRhs)
    )
    val expectedResult = DefnVarTraversalResult(traversedDefnVar, TheJavaModifiers)

    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheModListResult).when(modListTraverser).traverse(eqExpectedModifiers(transformedDefnVar, javaScope))
    doReturn(None).when(defnValOrVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  private def eqExpectedModifiers(defnVar: Defn.Var, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(defnVar, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
