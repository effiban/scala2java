package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
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
    val expectedModifiersContext = ModifiersContext(declVar, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
