import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_DOUBLE

inline fun MemorySegment.getMat (i:Int)
	= getAtIndex(JAVA_DOUBLE, i.toLong())

inline fun MemorySegment.setMat (i:Int, v:Double)
	= setAtIndex(JAVA_DOUBLE, i.toLong(), v)


@JvmInline
value class Mat4x4
internal constructor (val address: MemorySegment)
{
	operator fun get (i:Int) = address.getMat(i)
	operator fun get (x:Int, y:Int) = get((y shl 2) +x)

	operator fun set (i:Int, v:Double) = address.setMat(i, v)
	operator fun set (x:Int, y:Int, v:Double) = set((y shl 2)+x, v)

	companion object
	{
		operator fun invoke (
			arena:Arena,
			i0:Double, i1:Double, i2:Double, i3:Double,
			i4:Double, i5:Double, i6:Double, i7:Double,
			i8:Double, i9:Double, iA:Double, iB:Double,
			iC:Double, iD:Double, iE:Double, iF:Double,
		): Mat4x4
		{
			return Mat4x4(arena.allocate(JAVA_DOUBLE, 4*4).apply {
				setMat(0, i0);setMat(1, i1);setMat(2, i2);setMat(3, i3)
				setMat(4, i4);setMat(5, i5);setMat(6, i6);setMat(7, i7)
				setMat(8, i8);setMat(9, i9);setMat(10, iA);setMat(11, iB)
				setMat(12, iC);setMat(13, iD);setMat(14, iE);setMat(15, iF)
			})
		}

		operator fun invoke (
			i0:Double, i1:Double, i2:Double, i3:Double,
			i4:Double, i5:Double, i6:Double, i7:Double,
			i8:Double, i9:Double, iA:Double, iB:Double,
			iC:Double, iD:Double, iE:Double, iF:Double,
		): Mat4x4
		{
			return this(
				Arena.ofAuto(),
				i0,i1,i2,i3,
				i4,i5,i6,i7,
				i8,i9,iA,iB,
				iC,iD,iE,iF
			)
		}
	}
}

fun matStr (m:Mat4x4):String
{
	return """
		[ ${m[0]}, ${m[1]}, ${m[2]}, ${m[3]} ]
		[ ${m[4]}, ${m[5]}, ${m[6]}, ${m[7]} ]
		[ ${m[8]}, ${m[9]}, ${m[10]}, ${m[11]} ]
		[ ${m[12]}, ${m[13]}, ${m[14]}, ${m[15]} ]
	""".trimIndent()
}

fun main ()
{
	val m = Mat4x4(
		1.0, 0.0, 0.0, 0.0,
		0.0, 1.0, 0.0, 0.0,
		0.0, 0.0, 1.0, 0.0,
		0.0, 0.0, 0.0, 1.0,
	)

	val m2 = Mat4x4(
		2.0, 0.0, 0.0, 0.0,
		0.0, 3.0, 0.0, 0.0,
		0.0, 0.0, 4.0, 0.0,
		0.0, 0.0, 0.0, 5.0,
	)

	println(matStr(m))

}
