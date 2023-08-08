package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.transformers.{DefnVarToDeclVarTransformer, DefnVarTransformer}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Decl, Defn, Mod, Name, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnVarTraverserImplTest extends UnitTestSuite {

  private val TheAnnot = mod"@MyAnnotation"
  private val TheTransformedAnnot = mod"@MyTransformedAnnotation"
  private val TheTraversedAnnot = mod"@MyTraversedAnnotation"
  private val TheScalaMods = List(TheAnnot, Mod.Private(Name.Anonymous()))
  private val TheTransformedScalaMods = List(TheTransformedAnnot, Mod.Private(Name.Anonymous()))
  private val TheTraversedScalaMods = List(TheTraversedAnnot, Mod.Private(Name.Anonymous()))

  private val MyVarPat = p"myVar"
  private val MyTransformedVarPat = p"myTransformedVar"
  private val MyTraversedVarPat = p"myTraversedVar"

  private val TheType = t"MyType"
  private val TheTransformedType = t"MyTransformedType"
  private val TheTraversedType = t"MyTraversedType"

  private val TheRhs = q"3"
  private val TheTransformedRhs = q"33"
  private val TheTraversedRhs = q"333"

  private val statModListTraverser = mock[StatModListTraverser]
  private val defnVarTypeTraverser = mock[DefnVarTypeTraverser]
  private val patTraverser = mock[PatTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val declVarTraverser = mock[DeclVarTraverser]
  private val defnVarToDeclVarTransformer = mock[DefnVarToDeclVarTransformer]
  private val defnVarTransformer = mock[DefnVarTransformer]

  private val defnVarTraverser = new DefnVarTraverserImpl(
    statModListTraverser,
    defnVarTypeTraverser,
    patTraverser,
    expressionTermTraverser,
    declVarTraverser,
    defnVarToDeclVarTransformer,
    defnVarTransformer
  )

  test("traverse() when transformed to a Decl.Var, should traverse with the DeclVarTraverser") {
    val javaScope = JavaScope.Class

    val defnVar = Defn.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = Some(TheType),
      rhs = Some(TheRhs)
    )

    val declVar = Decl.Var(
      mods = TheScalaMods,
      pats = List(MyVarPat),
      decltpe = TheType
    )
    val traversedDeclVar = Decl.Var(
      mods = TheTraversedScalaMods,
      pats = List(MyTraversedVarPat),
      decltpe = TheTraversedType
    )

    val context = StatContext(javaScope)

    when(defnVarToDeclVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(Some(declVar))
    doReturn(traversedDeclVar).when(declVarTraverser).traverse(eqTree(declVar))

    defnVarTraverser.traverse(defnVar, context).structure shouldBe traversedDeclVar.structure
  }


  test("traverse() when not transformed to a Decl.Var, and it is a class member - typed with value") {
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

    when(defnVarToDeclVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(None)
    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheTransformedScalaMods))
    doReturn(Some(TheTraversedType))
      .when(defnVarTypeTraverser).traverse(eqSomeTree(TheTransformedType), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)).structure shouldBe traversedDefnVar.structure
  }

  test("traverse() when not transformed to a Decl.Var, and it is a class member - typed without value") {
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

    when(defnVarToDeclVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(None)
    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheTransformedScalaMods))
    doReturn(Some(TheTraversedType)).when(defnVarTypeTraverser).traverse(eqSomeTree(TheTransformedType), eqTo(None))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)).structure shouldBe traversedDefnVar.structure
  }

  test("traverse() when not transformed to a Decl.Var, and it is a class member - untyped with value - type inferred") {
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

    when(defnVarToDeclVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(None)
    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheTransformedScalaMods))
    doReturn(Some(TheTraversedType)).when(defnVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)).structure shouldBe traversedDefnVar.structure
  }

  test("traverse() when not transformed to a Decl.Var, and it is a class member - untyped with value - type not inferred") {
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

    when(defnVarToDeclVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(None)
    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheTransformedScalaMods))
    doReturn(None).when(defnVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)).structure shouldBe traversedDefnVar.structure
  }

  test("traverse() when not transformed to a Decl.Var, and it is an interface member - typed with value") {
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

    when(defnVarToDeclVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(None)
    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheTransformedScalaMods))
    doReturn(Some(TheTraversedType))
      .when(defnVarTypeTraverser).traverse(eqSomeTree(TheTransformedType), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)).structure shouldBe traversedDefnVar.structure
  }

  test("traverse() when not transformed to a Decl.Var, and it is an interface member - typed without value") {
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

    when(defnVarToDeclVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(None)
    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheTransformedScalaMods))
    doReturn(Some(TheTraversedType)).when(defnVarTypeTraverser).traverse(eqSomeTree(TheTransformedType), eqTo(None))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)).structure shouldBe traversedDefnVar.structure
  }

  test("traverse() when not transformed to a Decl.Var, and it is an interface member - untyped with value - type inferred") {
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

    when(defnVarToDeclVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(None)
    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheTransformedScalaMods))
    doReturn(Some(TheTraversedType)).when(defnVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)).structure shouldBe traversedDefnVar.structure
  }

  test("traverse() when not transformed to a Decl.Var, and it is an interface member - untyped with value - type not inferred") {
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

    when(defnVarToDeclVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(None)
    when(defnVarTransformer.transform(eqTree(defnVar), eqTo(javaScope))).thenReturn(transformedDefnVar)
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheTransformedScalaMods))
    doReturn(None).when(defnVarTypeTraverser).traverse(eqTo(None), eqSomeTree(TheTransformedRhs))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyTransformedVarPat))
    doReturn(TheTraversedRhs).when(expressionTermTraverser).traverse(eqTree(TheTransformedRhs))

    defnVarTraverser.traverse(defnVar, StatContext(javaScope)).structure shouldBe traversedDefnVar.structure
  }
}
