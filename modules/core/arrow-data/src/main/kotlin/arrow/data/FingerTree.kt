package arrow.data

import arrow.core.Option
import arrow.core.Tuple2
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import java.lang.UnsupportedOperationException

sealed abstract class FingerTree<out A> {

  abstract fun unconsL(): Option<Tuple2<A, FingerTree<A>>>
  abstract fun unconsR(): Option<Tuple2<A, FingerTree<A>>>

}

sealed abstract class Finger<out A> {
  abstract fun <B> foldMap(SG: Semigroup<B>, f: (A) -> B): B

  abstract fun lhead(): A
  abstract fun ltail(): Finger<A>
  abstract fun rhead(): A
  abstract fun rtail(): Finger<A>

  abstract fun <B> map(f: (A) -> B): Finger<B>
//  def toTree[V](implicit m: Measured[V, A]): FingerTree[V, A]
//  def measure[V](implicit m: Measured[V, A]) = FingerMeasured(m)(this)
}

fun <A> Finger<A>.append(a: A): Finger<A> =
  when (this) {
    is Empty -> One(a)
    is One -> Two(a, a1)
    is Two -> Three(a, a1, a2)
    is Three -> Four(a, a1, a2, a3)
    is Four -> throw UnsupportedOperationException("")
  }

fun <A> Finger<A>.prepend(a: A): Finger<A> =
  when (this) {
    is Empty -> One(a)
    is One -> Two(a1, a)
    is Two -> Three(a1, a2, a)
    is Three -> Four(a1, a2, a3, a)
    is Four -> throw UnsupportedOperationException("")
  }


object Empty : Finger<Nothing>() {
  override fun <B> foldMap(SG: Semigroup<B>, f: (Nothing) -> B): B = throw UnsupportedOperationException("")

  override fun lhead(): Nothing = throw UnsupportedOperationException("")
  override fun ltail(): Finger<Nothing> = throw UnsupportedOperationException("")

  override fun rhead(): Nothing = throw UnsupportedOperationException("")
  override fun rtail(): Finger<Nothing> = throw UnsupportedOperationException("")

  override fun <B> map(f: (Nothing) -> B): Finger<B> = throw UnsupportedOperationException("")

}

data class One<out A>(val a1: A) : Finger<A>() {
  override fun <B> foldMap(SG: Semigroup<B>, f: (A) -> B): B = f(a1)

  override fun lhead(): A = a1

  override fun ltail(): Finger<A> = Empty

  override fun rhead(): A = a1

  override fun rtail(): Finger<A> = Empty

  override fun <B> map(f: (A) -> B): Finger<B> = One(f(a1))

}

data class Two<out A>(val a1: A, val a2: A) : Finger<A>() {
  override fun <B> foldMap(SG: Semigroup<B>, f: (A) -> B): B =
    with(SG) { f(a1).combine(f(a2)) }

  override fun lhead(): A = a1

  override fun ltail(): Finger<A> = One(a2)

  override fun rhead(): A = a2

  override fun rtail(): Finger<A> = One(a1)

  override fun <B> map(f: (A) -> B): Finger<B> = Two(f(a1), f(a2))

}


data class Three<out A>(val a1: A, val a2: A, val a3: A) : Finger<A>() {
  override fun <B> foldMap(SG: Semigroup<B>, f: (A) -> B): B =
    with(SG) { f(a1) + f(a2) + f(a3) }

  override fun lhead(): A = a1

  override fun ltail(): Finger<A> = Two(a2, a3)

  override fun rhead(): A = a3

  override fun rtail(): Finger<A> = Two(a1, a2)

  override fun <B> map(f: (A) -> B): Finger<B> = Three(f(a1), f(a2), f(a3))

}

data class Four<out A>(val a1: A, val a2: A, val a3: A, val a4: A) : Finger<A>() {
  override fun <B> foldMap(SG: Semigroup<B>, f: (A) -> B): B =
    with(SG) { f(a1) + f(a2) + f(a3) + f(a4)}

  override fun lhead(): A = a1

  override fun ltail(): Finger<A> = Three(a2, a3, a4)

  override fun rhead(): A = a4

  override fun rtail(): Finger<A> = Three(a1, a2, a3)

  override fun <B> map(f: (A) -> B): Finger<B> = Four(f(a1), f(a2), f(a3), f(a4))

}