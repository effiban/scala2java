package io.github.effiban.scala2java.core.entities

import scala.meta.{Term, Type}

trait ParameterizedInitializerNameTypeMapping {

  def typeInitializedBy(qual: Term.Select): Option[Type.Ref]
}

object ParameterizedInitializerNameTypeMapping extends ParameterizedInitializerNameTypeMapping {

  private val InitializerQualToType = Map(
    TermSelects.ScalaRange -> TypeSelects.ScalaRange,
    TermSelects.ScalaOption -> TypeSelects.ScalaOption,
    TermSelects.ScalaSome -> TypeSelects.ScalaSome,
    TermSelects.ScalaRight -> TypeSelects.ScalaRight,
    TermSelects.ScalaLeft -> TypeSelects.ScalaLeft,
    TermSelects.ScalaTry -> TypeSelects.ScalaTry,
    TermSelects.ScalaSuccess -> TypeSelects.ScalaSuccess,
    TermSelects.ScalaFailure -> TypeSelects.ScalaFailure,
    TermSelects.ScalaFuture -> TypeSelects.ScalaFuture,
    TermSelects.ScalaStream -> TypeSelects.ScalaStream,
    TermSelects.ScalaArray -> TypeSelects.ScalaArray,
    TermSelects.ScalaList -> TypeSelects.ScalaList,
    TermSelects.ScalaVector -> TypeSelects.ScalaVector,
    TermSelects.ScalaSeq -> TypeSelects.ScalaSeq,
    TermSelects.ScalaSet -> TypeSelects.ScalaSet,
    TermSelects.ScalaMap -> TypeSelects.ScalaMap
  )
  // TODO add more collection initializers and durations

  def typeInitializedBy(qual: Term.Select): Option[Type.Ref] = TreeKeyedMap.get(InitializerQualToType, qual)
}
