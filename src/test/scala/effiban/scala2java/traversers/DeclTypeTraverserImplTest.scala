package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext}
import effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.matchers.JavaTreeTypeContextMatcher.eqJavaTreeTypeContext
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
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
  private val javaTreeTypeResolver = mock[JavaTreeTypeResolver]

  private val declTypeTraverser = new DeclTypeTraverserImpl(
    typeParamListTraverser,
    javaModifiersResolver,
    javaTreeTypeResolver)


  test("traverse()") {

    val declType = Decl.Type(
      mods = Modifiers,
      name = Type.Name("MyType"),
      tparams = TypeParams,
      bounds = Bounds(lo = None, hi = Some(Type.Name("T")))
    )

    whenResolveJavaTreeTypeThenReturnInterface(declType)
    whenResolveJavaModifiers(declType).thenReturn(List(JavaModifier.Private))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))

    declTypeTraverser.traverse(declType, StatContext(JavaScope.Class))

    outputWriter.toString shouldBe
      """
        |private interface MyType<T> {
        |}
        |""".stripMargin
  }

  private def whenResolveJavaTreeTypeThenReturnInterface(declType: Decl.Type): Unit = {
    val expectedJavaTreeTypeContext = JavaTreeTypeContext(declType, Modifiers)
    when(javaTreeTypeResolver.resolve(eqJavaTreeTypeContext(expectedJavaTreeTypeContext))).thenReturn(JavaTreeType.Interface)
  }

  private def whenResolveJavaModifiers(declType: Decl.Type) = {
    val expectedJavaModifiersContext = JavaModifiersContext(declType, Modifiers, JavaTreeType.Interface, JavaScope.Class)
    when(javaModifiersResolver.resolve(eqJavaModifiersContext(expectedJavaModifiersContext)))
  }

}
