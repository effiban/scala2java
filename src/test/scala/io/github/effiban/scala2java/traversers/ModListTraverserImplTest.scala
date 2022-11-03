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

  private val Mods = List(Mod.Private(Name.Anonymous()))
  private val Pats = List(Pat.Var(Term.Name("x")))
  private val TheDeclVal = Decl.Val(Mods, Pats, TypeNames.Int)
  private val TheModifiersContext = JavaModifiersContext(TheDeclVal, Mods, JavaTreeType.Variable, JavaScope.Class)

  private val annotListTraverser = mock[AnnotListTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val modListTraverser = new ModListTraverserImpl(annotListTraverser, javaModifiersResolver)

  test("traverse when annotations not on same line") {
    doWrite(
      """@MyAnnotation
      |""".stripMargin)
      .when(annotListTraverser).traverseMods(eqTreeList(Mods), onSameLine = ArgumentMatchers.eq(false))
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(TheModifiersContext)))
      .thenReturn(List(JavaModifier.Private, JavaModifier.Final))

    modListTraverser.traverse(TheModifiersContext)

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final """.stripMargin
  }

  test("traverse when annotations on same line") {
    doWrite("@MyAnnotation ")
      .when(annotListTraverser).traverseMods(eqTreeList(Mods), onSameLine = ArgumentMatchers.eq(true))
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(TheModifiersContext)))
      .thenReturn(List(JavaModifier.Private, JavaModifier.Final))

    modListTraverser.traverse(TheModifiersContext, annotsOnSameLine = true)

    outputWriter.toString shouldBe "@MyAnnotation private final "
  }
}
