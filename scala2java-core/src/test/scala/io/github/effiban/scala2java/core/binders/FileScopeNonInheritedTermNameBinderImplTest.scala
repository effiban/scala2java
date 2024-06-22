package io.github.effiban.scala2java.core.binders

import io.github.effiban.scala2java.core.declarationfinders.TreeTermNameDeclarationFinder
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Term.Block
import scala.meta.{Decl, Defn, Term, Tree, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

class FileScopeNonInheritedTermNameBinderImplTest extends UnitTestSuite {

  private val treeTermNameDeclarationFinder = mock[TreeTermNameDeclarationFinder]

  private val fileScopeNonInheritedTermNameBinder = new FileScopeNonInheritedTermNameBinderImpl(treeTermNameDeclarationFinder)

  test("bind object reference to declaration") {
    val pkg =
      q"""
      package a.b {
        object C {
          C.d
        }
      }
      """

    val obj = pkg.stats.head.asInstanceOf[Defn.Object]
    val objTempl = obj.templ
    val objMemberRef = objTempl.stats.head.asInstanceOf[Term.Select]
    val objTermName = objMemberRef.qual.asInstanceOf[Term.Name]

    doAnswer((tree: Tree, _: Term.Name) => tree match {
      case aTree if aTree.structure == pkg.structure => Some(obj)
      case _ => None
    }).when(treeTermNameDeclarationFinder).find(any[Tree], eqTree(objTermName))

    fileScopeNonInheritedTermNameBinder.bind(objTermName).value.structure shouldBe obj.structure
  }

  test("bind data member usage to declaration") {
    val traitA =
      q"""
      trait A {
        val b: Int
        b
      }
      """

    val traitATemplate = traitA.templ
    val declValB = traitATemplate.stats.head.asInstanceOf[Decl.Val]
    val b = traitATemplate.stats(1).asInstanceOf[Term.Name]

    doAnswer((tree: Tree, _: Term.Name) => tree match {
      case aTree if aTree.structure == traitATemplate.structure => Some(declValB)
      case _ => None
    }).when(treeTermNameDeclarationFinder).find(any[Tree], eqTree(b))

    fileScopeNonInheritedTermNameBinder.bind(b).value.structure shouldBe declValB.structure
  }

  test("bind method invocation to declaration") {
    val traitA =
      q"""
      trait A {
        def b(): Int
        b
      }
      """

    val traitATemplate = traitA.templ
    val declDefB = traitATemplate.stats.head.asInstanceOf[Decl.Def]
    val b = traitATemplate.stats(1).asInstanceOf[Term.Name]

    doAnswer((tree: Tree, _: Term.Name) => tree match {
      case aTree if aTree.structure == traitATemplate.structure => Some(declDefB)
      case _ => None
    }).when(treeTermNameDeclarationFinder).find(any[Tree], eqTree(b))

    fileScopeNonInheritedTermNameBinder.bind(b).value.structure shouldBe declDefB.structure
  }

  test("bind method param usage to declaration") {
    val traitA =
      q"""
      trait A {
        def b(c: Int): Int = c
      }
      """

    val traitATemplate = traitA.templ
    val defnDefB = traitATemplate.stats.head.asInstanceOf[Defn.Def]
    val paramC = defnDefB.paramss.flatten.collectFirst { case p@param"c: Int" => p }.get
    val c = defnDefB.body.asInstanceOf[Term.Name]

    doAnswer((tree: Tree, _: Term.Name) => tree match {
      case aTree if aTree.structure == defnDefB.structure => Some(paramC)
      case _ => None
    }).when(treeTermNameDeclarationFinder).find(any[Tree], eqTree(c))

    fileScopeNonInheritedTermNameBinder.bind(c).value.structure shouldBe paramC.structure
  }

  test("bind local variable usage to declaration") {
    val traitA =
      q"""
      trait A {
        def b(): Unit = {
          val c: Int = 3
          c
        }
      }
      """

    val traitATemplate = traitA.templ
    val defnDefB = traitATemplate.stats.head.asInstanceOf[Defn.Def]
    val block = defnDefB.body.asInstanceOf[Block]
    val defnValC = block.stats.head
    val c = block.stats(1).asInstanceOf[Term.Name]

    doAnswer((tree: Tree, _: Term.Name) => tree match {
      case aTree if aTree.structure == block.structure => Some(defnValC)
      case _ => None
    }).when(treeTermNameDeclarationFinder).find(any[Tree], eqTree(c))

    fileScopeNonInheritedTermNameBinder.bind(c).value.structure shouldBe defnValC.structure
  }

}
