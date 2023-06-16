package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.JavaModifier.{Final, Private}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchers

import scala.meta.{Decl, Mod, Name, Pat, Term}

class DeprecatedModListTraverserImplTest extends UnitTestSuite {

  private val PrivateFinalMods = List(Mod.Private(Name.Anonymous()), Mod.Final())
  private val ImplicitPrivateFinalMods = Mod.Implicit() +: PrivateFinalMods

  private val Pats = List(Pat.Var(Term.Name("x")))

  private val annotListTraverser = mock[DeprecatedAnnotListTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modListTraverser = new DeprecatedModListTraverserImpl(annotListTraverser, javaModifiersResolver)

  test("traverse when annotations not on same line") {
    val modifiersContext = modifiersContextOf(PrivateFinalMods)

    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(PrivateFinalMods), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolve(eqModifiersContext(modifiersContext)))
      .thenReturn(List(Private, Final))

    modListTraverser.traverse(modifiersContext)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final """.stripMargin
  }

  test("traverse when annotations on same line") {
    val modifiersContext = modifiersContextOf(PrivateFinalMods)

    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(eqTreeList(PrivateFinalMods), onSameLine = ArgumentMatchers.eq(true))
    when(javaModifiersResolver.resolve(eqModifiersContext(modifiersContext)))
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
    when(javaModifiersResolver.resolve(eqModifiersContext(modifiersContext)))
      .thenReturn(List(JavaModifier.Private, JavaModifier.Final))

    modListTraverser.traverse(modifiersContext)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |/* implicit */private final """.stripMargin
  }

  private def modifiersContextOf(mods: List[Mod]) = ModifiersContext(declValWith(mods), JavaTreeType.Variable, JavaScope.Class)

  private def declValWith(mods: List[Mod]) = Decl.Val(mods, Pats, TypeNames.Int)
}
