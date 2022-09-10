package effiban.scala2java.traversers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

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

  private val defnTypeTraverser = new DefnTypeTraverserImpl(
    typeParamListTraverser,
    typeTraverser,
    typeBoundsTraverser,
    javaModifiersResolver)


  test("traverse() when has body and no bounds") {
    val defnType = Defn.Type(
      mods = Modifiers,
      name = MyType,
      tparams = TypeParams,
      body = MyOtherType
    )

    whenResolveJavaModifiersThenReturnPrivate(defnType)
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("MyOtherType").when(typeTraverser).traverse(eqTree(MyOtherType))

    defnTypeTraverser.traverse(defnType)

    outputWriter.toString shouldBe
      """private interface MyType<T> extends MyOtherType {
        |}
        |""".stripMargin
  }

  test("traverse() when has no body and has upper bound") {
    val bounds = Bounds(lo = None, hi = Some(MyOtherType))
    val defnType = Defn.Type(
      mods = Modifiers,
      name = MyType,
      tparams = TypeParams,
      body = Type.AnonymousName(),
      bounds = bounds
    )

    whenResolveJavaModifiersThenReturnPrivate(defnType)
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doWrite("extends MyOtherType").when(typeBoundsTraverser).traverse(eqTree(bounds))

    defnTypeTraverser.traverse(defnType)

    outputWriter.toString shouldBe
      """private interface MyType<T> extends MyOtherType {
        |}
        |""".stripMargin
  }

  private def whenResolveJavaModifiersThenReturnPrivate(defnType: Defn.Type): Unit = {
    val expectedContext = JavaModifiersContext(defnType, Modifiers, JavaTreeType.Interface, javaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedContext))).thenReturn(List(JavaModifier.Private))
  }

}
