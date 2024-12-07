package io.github.effiban.scala2java.core.cleanup

import scala.meta.{Template, Type}

trait IsTemplateAncestorUsed extends ((Template , Type.Ref) => Boolean)
