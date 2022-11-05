package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.{Decl, Init, Mod, Name, Pat, Term, Type}

class DeclVarTraverserImplTest extends UnitTestSuite {

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val Modifiers = List(TheAnnot)
  private val IntType = TypeNames.Int
  private val MyVarPat = Pat.Var(Term.Name("myVar"))

  private val modListTraverser = mock[ModListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val patListTraverser = mock[PatListTraverser]

  private val declVarTraverser = new DeclVarTraverserImpl(
    modListTraverser,
    typeTraverser,
    patListTraverser
  )


  test("traverse() when it is a class member") {
    val javaScope = JavaScope.Class

    val declVar = Decl.Var(
      mods = Modifiers,
      pats = List(MyVarPat),
      decltpe = IntType
    )

    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
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
      .when(modListTraverser).traverse(eqExpectedModifiers(declVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
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
      .when(modListTraverser).traverse(eqExpectedModifiers(declVar, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int").when(typeTraverser).traverse(eqTree(IntType))
    doWrite("myVar").when(patListTraverser).traverse(eqTreeList(List(MyVarPat)))

    declVarTraverser.traverse(declVar, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVar""".stripMargin
  }

  private def eqExpectedModifiers(declVar: Decl.Var, javaScope: JavaScope) = {
    val expectedJavaModifiersContext = JavaModifiersContext(declVar, JavaTreeType.Variable, javaScope)
    eqJavaModifiersContext(expectedJavaModifiersContext)
  }
}
