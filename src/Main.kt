import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorSpecies
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_DOUBLE
import java.nio.ByteOrder


@JvmInline
value class Mat3x3
internal constructor (val address: MemorySegment)
{
	val wide get() = 3
	val tall get() = 3
	val count get() = 3*3

	operator fun get (i:Int) = address.getAtIndex(JAVA_DOUBLE, i.toLong())
	operator fun get (x:Int, y:Int) = get(y*3+x)

	operator fun set (i:Int, v:Double) = address.setAtIndex(JAVA_DOUBLE, i.toLong(), v)
	operator fun set (x:Int, y:Int, v:Double) = set(y*3+x, v)

	companion object
	{
		operator fun invoke (
			arena:Arena,
			i0:Double, i1:Double, i2:Double,
			i3:Double, i4:Double, i5:Double,
			i6:Double, i7:Double, i8:Double,
		): Mat3x3
		{
			return Mat3x3(arena.allocate(JAVA_DOUBLE, 3*3).apply {
				setAtIndex(JAVA_DOUBLE, 0, i0)
				setAtIndex(JAVA_DOUBLE, 1, i1)
				setAtIndex(JAVA_DOUBLE, 2, i2)
				setAtIndex(JAVA_DOUBLE, 3, i3)
				setAtIndex(JAVA_DOUBLE, 4, i4)
				setAtIndex(JAVA_DOUBLE, 5, i5)
				setAtIndex(JAVA_DOUBLE, 6, i6)
				setAtIndex(JAVA_DOUBLE, 7, i7)
				setAtIndex(JAVA_DOUBLE, 8, i8)
			})
		}

		operator fun invoke (
			i0:Double, i1:Double, i2:Double,
			i3:Double, i4:Double, i5:Double,
			i6:Double, i7:Double, i8:Double,
		): Mat3x3
		{
			return this(Arena.ofAuto(), i0,i1,i2,i3,i4,i5,i6,i7,i8)
		}
	}
}

fun matStr (m:Mat3x3):String
{
	return """
		[ ${m[0]}, ${m[1]}, ${m[2]} ]
		[ ${m[3]}, ${m[4]}, ${m[5]} ]
		[ ${m[6]}, ${m[7]}, ${m[8]} ]
	""".trimIndent()
}

val SPECIES =  DoubleVector.SPECIES_PREFERRED
val M_VEC_SIZE = run {
	var sz = 0
	val specSize = SPECIES.length()
	while (sz < (3*3))
	{
		sz += specSize
	}
	sz.toLong()
}

fun allocVectorMatrix (): MemorySegment
{
	return Arena.global().allocate(JAVA_DOUBLE, M_VEC_SIZE)
}

val TEMP_LHS = allocVectorMatrix()
val TEMP_RHS = allocVectorMatrix()
val TEMP_RESULT = allocVectorMatrix()

fun main ()
{
	val m = Mat3x3(
		1.0, 0.0, 0.0,
		0.0, 1.0, 0.0,
		0.0, 0.0, 1.0,
	)

	val m2 = Mat3x3(
		2.0, 0.0, 0.0,
		0.0, 3.0, 0.0,
		0.0, 0.0, 4.0,
	)


	val madr1 = m.address
	MemorySegment.copy(madr1, 0L, TEMP_LHS, 0L, 3L*3*Double.SIZE_BYTES)
//	TEMP_LHS.copyFrom(madr1)

	val madr2 = m2.address
	for (yy in 0..2L)
	{
		val muld = yy * 3
		for (xx in 0..2L)
		{
			TEMP_RHS.setAtIndex(JAVA_DOUBLE, muld+xx, madr2.getAtIndex(JAVA_DOUBLE, xx*3+yy))
		}
	}

	var i = 0
	val endp = m.count
	val step = SPECIES.length()

	while (i < endp)
	{
		val subi = i * Double.SIZE_BYTES.toLong()
		val mv = DoubleVector.fromMemorySegment(SPECIES, TEMP_LHS, subi, ByteOrder.nativeOrder())
		val mv2 = DoubleVector.fromMemorySegment(SPECIES, TEMP_RHS, subi, ByteOrder.nativeOrder())
		val res = mv.mul(mv2)
		res.intoMemorySegment(TEMP_RESULT, subi, ByteOrder.nativeOrder())
		i += step
	}
	MemorySegment.copy(TEMP_RESULT, 0L, madr1, 0L, 3L*3*Double.SIZE_BYTES)
	println(matStr(m))

}
