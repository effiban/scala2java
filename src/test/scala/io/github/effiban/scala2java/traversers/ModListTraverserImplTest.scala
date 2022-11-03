package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.JavaModifiersContext
import io.github.effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import io.github.effiban.scala2java.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.{Decl, Mod, Name, Pat, Term}

class ModListTraverserImplTest extends UnitTestSuite {

  private val PrivateFinalMods = List(Mod.Private(Name.Anonymous()), Mod.Final())
  private val ImplicitPrivateFinalMods = Mod.Implicit() +: PrivateFinalMods

  private val Pats = List(Pat.Var(Term.Name("x")))

  private val annotListTraverser = mock[AnnotListTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modListTraverser = new ModListTraverserImpl(annotListTraverser, javaModifiersResolver)

  test("traverse when annotations not on same line") {
    val modifiersContext = modifiersContextOf(PrivateFinalMods)

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(PrivateFinalMods), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(modifiersContext)))
      .thenReturn(List(JavaModifier.Private, JavaModifier.Final))

    modListTraverser.traverse(modifiersContext)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final """.stripMargin
  }

  test("traverse when annotations on same line") {
    val modifiersContext = modifiersContextOf(PrivateFinalMods)

    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(eqTreeList(PrivateFinalMods), onSameLine = ArgumentMatchers.eq(true))
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(modifiersContext)))
      .thenReturn(List(JavaModifier.Private, JavaModifier.Final))

    modListTraverser.traverse(modifiersContext, annotsOnSameLine = true)

    outputWriter.toString shouldBe "@MyAnnotation private final "
  }

  test("traverse when 'implicit'") {
    val modifiersContext = modifiersContextOf(ImplicitPrivateFinalMods)

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(ImplicitPrivateFinalMods), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(modifiersContext)))
      .thenReturn(List(JavaModifier.Private, JavaModifier.Final))

    modListTraverser.traverse(modifiersContext)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |/* implicit */private final """.stripMargin
  }

  private def modifiersContextOf(mods: List[Mod]) = JavaModifiersContext(declValWith(mods), mods, JavaTreeType.Variable, JavaScope.Class)

  private def declValWith(mods: List[Mod]) = Decl.Val(mods, Pats, TypeNames.Int)
}
