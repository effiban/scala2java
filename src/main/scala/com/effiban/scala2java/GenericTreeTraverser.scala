package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._

import scala.meta.Name.Indeterminate
import scala.meta.{Case, Decl, Defn, Import, Importee, Importer, Init, Mod, Name, Pat, Pkg, Source, Template, Term, Tree, Type}

object GenericTreeTraverser extends ScalaTreeTraverser[Tree] {

  override def traverse(tree: Tree): Unit = tree match {
    case source: Source => SourceTraverser.traverse(source)
    case pkg: Pkg => PkgTraverser.traverse(pkg)
    case template: Template => TemplateTraverser.traverse(template)
    case init: Init => InitTraverser.traverse(init)

    case `import`: Import => ImportTraverser.traverse(`import`)
    case importer: Importer => ImporterTraverser.traverse(importer)
    case importee: Importee => ImporteeTraverser.traverse(importee)

    case decl: Decl => DeclTraverser.traverse(decl)
    case defn: Defn => DefnTraverser.traverse(defn)

    case termRef: Term.Ref => TermRefTraverser.traverse(termRef)
    case term: Term => TermTraverser.traverse(term)
    case termParam: Term.Param => TermParamTraverser.traverse(termParam)

    case typeRef: Type.Ref => TypeRefTraverser.traverse(typeRef)
    case `type`: Type => TypeTraverser.traverse(`type`)
    case typeBounds: Type.Bounds => TypeBoundsTraverser.traverse(typeBounds)
    case typeParam: Type.Param => TypeParamTraverser.traverse(typeParam)

    case pat: Pat => PatTraverser.traverse(pat)
    case `case`: Case => CaseTraverser.traverse(`case`)

    case annotation: Mod.Annot => AnnotTraverser.traverse(annotation)

    case anonymousName: Name.Anonymous => NameAnonymousTraverser.traverse(anonymousName)
    case indeterminateName: Indeterminate => NameIndeterminateTraverser.traverse(indeterminateName)

    case _ => emitComment(s"UNSUPPORTED: $tree")
  }
}
