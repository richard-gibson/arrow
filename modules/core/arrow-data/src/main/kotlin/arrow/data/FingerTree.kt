package arrow.data

import arrow.core.identity
import arrow.typeclasses.Monoid
import arrow.typeclasses.Reducer
import arrow.typeclasses.UnitReducer
import arrow.typeclasses.Semigroup


sealed abstract class FingerTree<V, A>(val measurer: Reducer<A, V>, val M: Monoid<V>) {

  fun measure(): V = fingerTreeMeasure<A, V>(M).unit(this)


  fun <B> foldMap(MB: Monoid<B>, f: (A) -> B): B {
    return fold({ MB.empty() }, { _, x -> f(x) },
      { v: V, pr: Finger<V, A>, m: FingerTree<V, Node<V, A>>, sf: Finger<V, A> ->
      with(MB) {
        pr.foldMap(this, f) + (m.foldMap(MB){x -> x.foldMap(MB){sf.foldMap(MB, f)}})
      }
    })
  }


  fun <B> foldRight(z: () -> B, f: (A, B) -> B): B =
    foldMap(M)
      /**
       * Fold over the structure of the tree. The given functions correspond to the three possible variations of the finger tree.
       *
       * @param empty if the tree is empty, convert the measure to a `B`
       * @param single if the tree contains a single element, convert the measure and this element to a `B`
       * @param deep otherwise, convert the measure, the two fingers, and the sub tree to a `B`.
       */
      abstract fun <B> fold(empty: (V) -> B, single: (V, A) -> B,
                            deep: (V, Finger<V, A>, FingerTree<V, Node<V, A>>, Finger<V, A>) -> B): B


}

sealed abstract class Finger<V, A> {
  abstract fun measure(): V
  abstract fun <B> foldMap(SG: Semigroup<B>, f: (A) -> B): B

}


sealed abstract class Node<V, A>(R: Reducer<A, V>, M: Monoid<V>) {

  abstract fun <B> fold(two: (V, A, A) -> B, three: (V, A, A, A) -> B): B
  fun <B> foldMap(SG: Semigroup<B>, f: (A) -> B): B =
    fold({ _, a1, a2 -> with(SG) { f(a1) + f(a2) } },
      {_,  a1, a2, a3 -> with(SG){f(a1) + f(a2) + f(a3)}})



}

fun <A, V> fingerMeasure(SG: Semigroup<V>): Reducer<Finger<V, A>, V> =
  UnitReducer(SG){ a -> a.measure()}

fun <A, V> fingerTreeMeasure(SG: Semigroup<V>): Reducer<FingerTree<V, A>, V> =
  UnitReducer(SG){ a -> a.fold(::identity, {v, _ -> v}, {v, _, _, _ -> v})}