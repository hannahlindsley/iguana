package org.iguana.parser.datadependent.env

import org.iguana.datadependent.env.Environment
import org.iguana.datadependent.env.array.ArrayEnvironment
import org.scalatest.FunSuite

/**
  * Created by Anastasia Izmaylova
  */
class Test1 extends FunSuite {

  val empty = ArrayEnvironment.EMPTY

  test("test1") {
    val a1: Array[java.lang.Integer] = Array(1, 2, 3, 4, 5)
    val env1: Environment = empty.declare(a1:_*)
    val a2: Array[java.lang.Integer] = Array(1, 2, 3, 6, 5)
    val env2: Environment = empty.declare(a2:_*)
    val env3: Environment = env2.store(3, 4)

    assert(env1.hashCode() == env3.hashCode())
    assert(env1.equals(env3) && env3.equals(env1))
  }

  test("test2") {
    val a1: Array[java.lang.Integer] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
    val env1: Environment = empty.declare(a1:_*)
    val a2: Array[java.lang.Integer] = Array(1, 2, 3, 6, 5, 6, 7, 8, 9, 10, 10, 12, 12, 14, 15)
    val env2: Environment = empty.declare(a2:_*)
    val env3: Environment = env2.store(3, 4).store(10, 11).store(12, 13)

    assert(env1.hashCode() == env3.hashCode())
    assert(env1.equals(env3) && env3.equals(env1))
  }

  test("test3") {
    val a1: Array[Tuple2[java.lang.Integer, java.lang.Integer]]
      = Array(Tuple2(1,2), Tuple2(2,3), Tuple2(3,4), Tuple2(4,5), Tuple2(5,6), Tuple2(6,7), Tuple2(7,8), Tuple2(8,9),
              Tuple2(9,10), Tuple2(10,11), Tuple2(11,12), Tuple2(12,13), Tuple2(13,14), Tuple2(14,15), Tuple2(15,16))
    val env1: Environment = empty.declare(a1:_*)
    val a2: Array[Tuple2[java.lang.Integer, java.lang.Integer]]
      = Array(Tuple2(1,2), Tuple2(2,3), Tuple2(3,4), Tuple2(6,6), Tuple2(5,6), Tuple2(6,7), Tuple2(7,8), Tuple2(8,9),
              Tuple2(9,10), Tuple2(10,11), Tuple2(11,11), Tuple2(12,13), Tuple2(13,13), Tuple2(14,15), Tuple2(15,16))
    val env2: Environment = empty.declare(a2:_*)
    val env3: Environment = env2.store(3, Tuple2(4,5)).store(10, Tuple2(11,12)).store(12, Tuple2(13,14))

    assert(env1.hashCode() == env3.hashCode())
    assert(env1.equals(env3) && env3.equals(env1))
  }

}
