package effiban.scala2java.traversers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

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

  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val declTypeTraverser = new DeclTypeTraverserImpl(
    typeParamListTraverser,
    javaModifiersResolver)


  test("traverse()") {

    val declType = Decl.Type(
      mods = Modifiers,
      name = Type.Name("MyType"),
      tparams = TypeParams,
      bounds = Bounds(lo = None, hi = Some(Type.Name("T")))
    )

    whenResolveJavaModifiers(declType).thenReturn(List(JavaModifier.Private))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))

    declTypeTraverser.traverse(declType)

    outputWriter.toString shouldBe
      """
        |private interface MyType<T> {
        |}
        |""".stripMargin
  }

  private def whenResolveJavaModifiers(declType: Decl.Type) = {
    val expectedContext = JavaModifiersContext(declType, Modifiers, JavaTreeType.Interface, javaScope)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedContext)))
  }
}
