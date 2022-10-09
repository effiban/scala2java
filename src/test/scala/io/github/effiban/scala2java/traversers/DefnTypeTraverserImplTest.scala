package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext}
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import io.github.effiban.scala2java.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Type.Bounds
import scala.meta.{Defn, Init, Mod, Name, Type}

class DefnTypeTraverserImplTest extends UnitTestSuite {

  private val Modifiers: List[Mod.Annot] = List(
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

  private val MyType = Type.Name("MyType")
  private val MyOtherType = Type.Name("MyOtherType")

  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val typeBoundsTraverser = mock[TypeBoundsTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]

  private val defnTypeTraverser = new DefnTypeTraverserImpl(
    typeParamListTraverser,
    typeTraverser,
    typeBoundsTraverser,
    javaModifiersResolver,
    javaTreeTypeResolver)


  test("traverse() when has body and no bounds") {
    val javaScope = JavaScope.Class

    val defnType = Defn.Type(
      mods = Modifiers,
      name = MyType,
      tparams = TypeParams,
      body = MyOtherType
    )

    whenResolveJavaTreeTypeThenReturnInterface(defnType)
    whenResolveJavaModifiersThenReturnPrivate(defnType, javaScope)
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("MyOtherType").when(typeTraverser).traverse(eqTree(MyOtherType))

    defnTypeTraverser.traverse(defnType, StatContext(javaScope))

    outputWriter.toString shouldBe
      """private interface MyType<T> extends MyOtherType {
        |}
        |""".stripMargin
  }

  test("traverse() when has no body and has upper bound") {
    val javaScope = JavaScope.Class

    val bounds = Bounds(lo = None, hi = Some(MyOtherType))
    val defnType = Defn.Type(
      mods = Modifiers,
      name = MyType,
      tparams = TypeParams,
      body = Type.AnonymousName(),
      bounds = bounds
    )

    whenResolveJavaTreeTypeThenReturnInterface(defnType)
    whenResolveJavaModifiersThenReturnPrivate(defnType, javaScope)
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("extends MyOtherType").when(typeBoundsTraverser).traverse(eqTree(bounds))

    defnTypeTraverser.traverse(defnType, StatContext(javaScope))

    outputWriter.toString shouldBe
      """private interface MyType<T> extends MyOtherType {
        |}
        |""".stripMargin
  }

  private def whenResolveJavaTreeTypeThenReturnInterface(defnType: Defn.Type): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(defnType, Modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(JavaTreeType.Interface)
  }

  private def whenResolveJavaModifiersThenReturnPrivate(defnType: Defn.Type, javaScope: JavaScope): Unit = {
    val expectedJavModifiersContext = JavaModifiersContext(defnType, Modifiers, JavaTreeType.Interface, javaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedJavModifiersContext))).thenReturn(List(JavaModifier.Private))
  }
}
