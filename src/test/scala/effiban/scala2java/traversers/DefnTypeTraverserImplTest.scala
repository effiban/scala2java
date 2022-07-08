package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.{JavaModifiersResolver, UnitTestSuite}

import scala.meta.Type.Bounds
import scala.meta.{Defn, Init, Mod, Name, Type}

class DefnTypeTraverserImplTest extends UnitTestSuite {

  private val JavaModifier = "private"

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
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val defnTypeTraverser = new DefnTypeTraverserImpl(
  typeParamListTraverser,
  typeTraverser,
  javaModifiersResolver)


  test("traverse() when has body and no bounds") {
    val defnType = Defn.Type(
      mods = Modifiers,
      name = MyType,
      tparams = TypeParams,
      body = MyOtherType
    )

    when(javaModifiersResolver.resolveForInterface(eqTreeList(Modifiers))).thenReturn(List(JavaModifier))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("MyOtherType").when(typeTraverser).traverse(eqTree(MyOtherType))

    defnTypeTraverser.traverse(defnType)

    outputWriter.toString shouldBe
      """private interface MyType<T> extends MyOtherType {
        |}
        |""".stripMargin
  }

  test("traverse() when has no body and has upper bound") {
    val defnType = Defn.Type(
      mods = Modifiers,
      name = MyType,
      tparams = TypeParams,
      body = Type.AnonymousName(),
      bounds = Bounds(lo = None, hi = Some(MyOtherType))
    )

    when(javaModifiersResolver.resolveForInterface(eqTreeList(Modifiers))).thenReturn(List(JavaModifier))
    doWrite("MyOtherType").when(typeTraverser).traverse(eqTree(MyOtherType))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))

    defnTypeTraverser.traverse(defnType)

    outputWriter.toString shouldBe
      """private interface MyType<T> extends MyOtherType {
        |}
        |""".stripMargin
  }

  test("traverse() when has no body and has lower bound") {
    val defnType = Defn.Type(
      mods = Modifiers,
      name = MyType,
      tparams = TypeParams,
      body = Type.AnonymousName(),
      bounds = Bounds(lo = Some(MyOtherType), hi = None)
    )

    when(javaModifiersResolver.resolveForInterface(eqTreeList(Modifiers))).thenReturn(List(JavaModifier))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))

    defnTypeTraverser.traverse(defnType)

    outputWriter.toString shouldBe
      """private interface MyType<T>/* super MyOtherType */ {
        |}
        |""".stripMargin
  }
}
