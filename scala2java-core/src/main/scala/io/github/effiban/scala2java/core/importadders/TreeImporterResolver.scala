package io.github.effiban.scala2java.core.importadders

import scala.meta.{Importer, Tree, Type}

trait TreeImporterResolver {
  def resolve(tree: Tree): List[Importer]
}

private[importadders] class TreeImporterResolverImpl(typeSelectImporterResolver: TypeSelectImporterResolver) extends TreeImporterResolver {

  override def resolve(tree: Tree): List[Importer] = {
    tree.collect {
      case typeSelect: Type.Select => typeSelectImporterResolver.resolve(typeSelect)
      // TODO resolve for Term.Select-s, using semantic information if possible
    }
  }
}

object TreeImporterResolver extends TreeImporterResolverImpl(TypeSelectImporterResolver)
