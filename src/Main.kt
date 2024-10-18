import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_DOUBLE


@JvmInline
value class Mat4x4
internal constructor (val address: MemorySegment)
{
	companion object
	{
		operator fun invoke (
			arena:Arena,
			i0:Double,i1:Double,i2:Double,i3:Double,
			i4:Double,i5:Double,i6:Double,i7:Double,
			i8:Double,i9:Double,iA:Double,iB:Double,
			iC:Double,iD:Double,iE:Double,iF:Double,
		): Mat4x4
		{
			return Mat4x4(arena.allocate(JAVA_DOUBLE, 4*4).apply {
				setAtIndex(JAVA_DOUBLE, 0, i0)
				setAtIndex(JAVA_DOUBLE, 1, i1)
				setAtIndex(JAVA_DOUBLE, 2, i2)
				setAtIndex(JAVA_DOUBLE, 3, i3)
				setAtIndex(JAVA_DOUBLE, 4, i4)
				setAtIndex(JAVA_DOUBLE, 5, i5)
				setAtIndex(JAVA_DOUBLE, 6, i6)
				setAtIndex(JAVA_DOUBLE, 7, i7)
				setAtIndex(JAVA_DOUBLE, 8, i8)
				setAtIndex(JAVA_DOUBLE, 9, i9)
				setAtIndex(JAVA_DOUBLE, 10, iA)
				setAtIndex(JAVA_DOUBLE, 11, iB)
				setAtIndex(JAVA_DOUBLE, 12, iC)
				setAtIndex(JAVA_DOUBLE, 13, iD)
				setAtIndex(JAVA_DOUBLE, 14, iE)
				setAtIndex(JAVA_DOUBLE, 15, iF)
			})
		}

		operator fun invoke (
			i0:Double,i1:Double,i2:Double,i3:Double,
			i4:Double,i5:Double,i6:Double,i7:Double,
			i8:Double,i9:Double,iA:Double,iB:Double,
			iC:Double,iD:Double,iE:Double,iF:Double,
		): Mat4x4
		{
			return this(Arena.ofAuto(), i0,i1,i2,i3,i4,i5,i6,i7,i8,i9,iA,iB,iC,iD,iE,iF)
		}
	}
}

fun main ()
{
	val m = Mat4x4(
		1.0, 0.0, 0.0, 0.0,
		0.0, 1.0, 0.0, 0.0,
		0.0, 0.0, 1.0, 0.0,
		0.0, 0.0, 0.0, 1.0,
	)
	println("Hello World!")
}
