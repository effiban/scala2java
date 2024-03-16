package io.github.effiban.scala2java.core.entities

import scala.meta.{Term, Type}

trait ParameterizedInitializerNameTypeMapping {

  def typeInitializedBy(qual: Term.Select): Option[Type.Ref]

  @deprecated
  def typeInitializedBy(termName: Term.Name): Option[Type.Ref]
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

  @deprecated
  private val InitializerNameToType = Map(
    Term.Name(TermNameValues.ScalaRange) -> TypeSelects.ScalaRange,
    Term.Name(TermNameValues.ScalaOption) -> TypeSelects.ScalaOption,
    Term.Name(TermNameValues.ScalaSome) -> TypeSelects.ScalaSome,
    Term.Name(TermNameValues.ScalaRight) -> TypeSelects.ScalaRight,
    Term.Name(TermNameValues.ScalaLeft) -> TypeSelects.ScalaLeft,
    Term.Name(TermNameValues.Try) -> TypeSelects.ScalaTry,
    Term.Name(TermNameValues.ScalaSuccess) -> TypeSelects.ScalaSuccess,
    Term.Name(TermNameValues.ScalaFailure) -> TypeSelects.ScalaFailure,
    Term.Name(TermNameValues.Future) -> TypeSelects.ScalaFuture,
    Term.Name(TermNameValues.Stream) -> TypeSelects.ScalaStream,
    Term.Name(TermNameValues.ScalaArray) -> TypeSelects.ScalaArray,
    Term.Name(TermNameValues.List) -> TypeSelects.ScalaList,
    Term.Name(TermNameValues.ScalaVector) -> TypeSelects.ScalaVector,
    Term.Name(TermNameValues.Seq) -> TypeSelects.ScalaSeq,
    Term.Name(TermNameValues.Set) -> TypeSelects.ScalaSet,
    Term.Name(TermNameValues.Map) -> TypeSelects.ScalaMap
  )


  def typeInitializedBy(qual: Term.Select): Option[Type.Ref] = TreeKeyedMap.get(InitializerQualToType, qual)

  @deprecated
  def typeInitializedBy(termName: Term.Name): Option[Type.Ref] =
    InitializerNameToType
      .find { case(aTermName, _) => aTermName.structure == termName.structure }
      .map { case (_, typeName) => typeName }
}
