package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Decl, Mod, Name, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteMod, XtensionQuasiquoteType}

class DeclVarTraverserImplTest extends UnitTestSuite {

  private val TheAnnot = mod"@MyAnnotation"
  private val TheTraversedAnnot = mod"@MyTraversedAnnotation"
  private val TheScalaMods = List(TheAnnot, Mod.Private(Name.Anonymous()))
  private val TheTraversedScalaMods = List(TheTraversedAnnot, Mod.Private(Name.Anonymous()))

  private val MyVarPat = p"myVar"
  private val MyTraversedVarPat = p"myTraversedVar"

  private val TheType = t"MyType"
  private val TheTraversedType = t"MyTraversedType"

  private val TheDeclVar = Decl.Var(
    mods = TheScalaMods,
    pats = List(MyVarPat),
    decltpe = TheType
  )
  private val TheTraversedDeclVar = Decl.Var(
    mods = TheTraversedScalaMods,
    pats = List(MyTraversedVarPat),
    decltpe = TheTraversedType
  )
  private val statModListTraverser = mock[StatModListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val patTraverser = mock[PatTraverser]

  private val declVarTraverser = new DeclVarTraverserImpl(
    statModListTraverser,
    typeTraverser,
    patTraverser
  )


  test("traverse() when it is a class member") {
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))

    declVarTraverser.traverse(TheDeclVar).structure shouldBe TheTraversedDeclVar.structure
  }

  test("traverse() when it is an interface member") {
    doReturn(TheTraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TheScalaMods))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doReturn(MyTraversedVarPat).when(patTraverser).traverse(eqTree(MyVarPat))

    declVarTraverser.traverse(TheDeclVar).structure shouldBe TheTraversedDeclVar.structure
  }
}
