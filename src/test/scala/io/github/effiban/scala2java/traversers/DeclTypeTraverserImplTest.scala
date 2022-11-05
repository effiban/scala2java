package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaTreeTypeContext, ModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.resolvers.JavaTreeTypeResolver
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Type.Bounds
import scala.meta.{Decl, Init, Mod, Name, Type}

class DeclTypeTraverserImplTest extends UnitTestSuite {

  private val Modifiers: List[Mod] = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )

  private val TypeParams = List(
    Type.Param(
      mods = List(),
      name = Type.Name("T"),
      tparams = List(),
      tbounds = Bounds(lo = None, hi = None),
      vbounds = List(),
      cbounds = List()
    )
  )

  private val modListTraverser = mock[ModListTraverser]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]

  private val declTypeTraverser = new DeclTypeTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    javaTreeTypeResolver)


  test("traverse()") {

    val declType = Decl.Type(
      mods = Modifiers,
      name = Type.Name("MyType"),
      tparams = TypeParams,
      bounds = Bounds(lo = None, hi = Some(Type.Name("T")))
    )

    whenResolveJavaTreeTypeThenReturnInterface(declType)
    doWrite(
      """@MyAnnotation
        |private """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(declType), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))

    declTypeTraverser.traverse(declType, StatContext(JavaScope.Class))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |private interface MyType<T> {
        |}
        |""".stripMargin
  }

  private def whenResolveJavaTreeTypeThenReturnInterface(declType: Decl.Type): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(declType, Modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(JavaTreeType.Interface)
  }

  private def eqExpectedModifiers(declType: Decl.Type) = {
    val expectedModifiersContext = ModifiersContext(declType, JavaTreeType.Interface, JavaScope.Class)
    eqModifiersContext(expectedModifiersContext)
  }
}
