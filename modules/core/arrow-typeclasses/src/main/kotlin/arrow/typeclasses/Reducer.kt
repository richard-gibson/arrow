package arrow.typeclasses


interface Reducer<C, M> {
  fun semigroup(): Semigroup<M>

  fun unit(c: C): M

  /** Fast `append(m, unit(c)) */
  fun snoc(m: M, c: C): M

  /** Fast `append(unit(c), m) */
  fun cons(c: C, m: M): M

  fun append(a1: M, a2: () -> M): M =
    with(semigroup()) { a1.combine(a2()) }


}

class UnitReducer<C, M>(private val SG: Semigroup<M>, private val u: (C) -> M) : Reducer<C, M> {
  override fun semigroup(): Semigroup<M> = SG
  override fun unit(c: C): M = u(c)
  override fun snoc(m: M, c: C): M = with(semigroup()) { m.combine(unit(c)) }
  override fun cons(c: C, m: M): M = with(semigroup()) { unit(c).combine(m) }
}