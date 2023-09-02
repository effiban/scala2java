package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.TreeElemSet
import io.github.effiban.scala2java.core.entities.TypeSelects._

import scala.meta.Type

trait TypeRefClassifier extends TypeClassifier[Type.Ref]

object TypeRefClassifier extends TypeRefClassifier {

  final val JavaStreamLike: Set[Type.Ref] = Set(
    ScalaLazyList,
    ScalaStream,
  )

  final val JavaListLike: Set[Type.Ref] = Set(
    ScalaSeq,
    ScalaArraySeq,
    ScalaLinearSeq,
    ScalaIndexedSeq,
    ScalaList,
    ScalaVector
  )

  final val JavaSetLike: Set[Type.Ref] = Set(
    ScalaSet,
    ScalaHashSet
  )

  final val JavaMapLike: Set[Type.Ref] = Set(
    ScalaMap,
    ScalaHashMap
  )

  final val IterableLike: Set[Type.Ref] =
    JavaStreamLike ++
      JavaListLike ++
      JavaSetLike ++
      Set(ScalaListSet, ScalaSortedSet, ScalaTreeSet) ++
      JavaMapLike ++
      Set(ScalaListMap, ScalaSortedMap, ScalaTreeMap)


  override def isJavaStreamLike(tpe: Type.Ref): Boolean = contains(JavaStreamLike, tpe)

  override def isJavaListLike(tpe: Type.Ref): Boolean = contains(JavaListLike, tpe)

  override def isJavaSetLike(tpe: Type.Ref): Boolean = contains(JavaSetLike, tpe)

  override def isJavaMapLike(tpe: Type.Ref): Boolean = contains(JavaMapLike, tpe)

  private def contains(set: Set[Type.Ref], tpe: Type.Ref): Boolean = TreeElemSet.contains(set, tpe)
}
