package io.github.effiban.scala2java.core.contexts

import scala.meta.{Mod, Tree}

case class JavaTreeTypeContext(tree: Tree, mods: List[Mod])
