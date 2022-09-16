package effiban.scala2java.traversers

import effiban.scala2java.contexts.StatContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.Type.Bounds
import scala.meta.{Decl, Pat, Term, Type}

class DeclTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaTreeType.Class)

  private val declValTraverser =  mock[DeclValTraverser]
  private val declVarTraverser =  mock[DeclVarTraverser]
  private val declDefTraverser =  mock[DeclDefTraverser]
  private val declTypeTraverser = mock[DeclTypeTraverser]

  private val declTraverser = new DeclTraverserImpl(
    declValTraverser,
    declVarTraverser,
    declDefTraverser,
    declTypeTraverser)

  test("traverse() a Decl.Val") {

    val declVal = Decl.Val(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = TypeNames.Int
    )

    declTraverser.traverse(declVal, TheStatContext)

    verify(declValTraverser).traverse(eqTree(declVal), ArgumentMatchers.eq(TheStatContext))
  }

  test("traverse() a Decl.Var") {

    val declVar = Decl.Var(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = TypeNames.Int
    )

    declTraverser.traverse(declVar, TheStatContext)

    verify(declVarTraverser).traverse(eqTree(declVar), ArgumentMatchers.eq(TheStatContext))
  }

  test("traverse() a Decl.Def") {

    val declDef = Decl.Def(
      mods = List(),
      name = Term.Name("myMethod"),
      tparams = List(),
      paramss = List(),
      decltpe = TypeNames.Int
    )

    declTraverser.traverse(declDef, TheStatContext)

    verify(declDefTraverser).traverse(eqTree(declDef), ArgumentMatchers.eq(TheStatContext))
  }

  test("traverse() a Decl.Type") {

    val declType = Decl.Type(
      mods = List(),
      name = Type.Name("MyType"),
      tparams = List(),
      bounds = Bounds(lo = None, hi = None)
    )

    declTraverser.traverse(declType, TheStatContext)

    verify(declTypeTraverser).traverse(eqTree(declType), ArgumentMatchers.eq(TheStatContext))
  }
}
