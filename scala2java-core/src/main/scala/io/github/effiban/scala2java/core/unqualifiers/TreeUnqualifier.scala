package io.github.effiban.scala2java.core.unqualifiers

import scala.meta.{Import, Importer, Pkg, Term, Transformer, Tree, Type}

trait TreeUnqualifier {
  def unqualify(tree: Tree, importers: List[Importer] = Nil): Tree
}

private[unqualifiers] class TreeUnqualifierImpl(termSelectUnqualifier: TermSelectUnqualifier,
                                                typeSelectUnqualifier: TypeSelectUnqualifier,
                                                typeProjectUnqualifier: TypeProjectUnqualifier) extends TreeUnqualifier {

  override def unqualify(tree: Tree, importers: List[Importer] = Nil): Tree = {
    new UnqualifyingTransformer(importers)(tree)
  }

  private class UnqualifyingTransformer(importers: List[Importer]) extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case termSelect: Term.Select => termSelectUnqualifier.unqualify(termSelect, importers) match {
          case aTermSelect: Term.Select => super.apply(aTermSelect)
          case aTermRef => aTermRef
        }
        case typeSelect: Type.Select => typeSelectUnqualifier.unqualify(typeSelect, importers)
        case typeProject: Type.Project => typeProjectUnqualifier.unqualify(typeProject, importers) match {
          case aTypeProject: Type.Project => super.apply(aTypeProject)
          case aTypeRef => aTypeRef
        }
        case `import`: Import => `import`// TODO handle imports below top level
        case pkg: Pkg => pkg // TODO handle nested packages
        case aTree => super.apply(aTree)
      }
  }
}
