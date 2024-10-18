import java.text.FieldPosition
import java.text.NumberFormat
import java.text.ParsePosition

val fmt = object : NumberFormat()
{
	override fun format(number: Double, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer
	{
		return toAppendTo!!.run {
			append("%.8f".format(number))
			this
		}
	}

	override fun format(number: Long, toAppendTo: StringBuffer?, pos: FieldPosition?): StringBuffer
	{
		TODO("Not yet implemented")
	}

	override fun parse(source: String?, parsePosition: ParsePosition?): Number
	{
		TODO("Not yet implemented")
	}

}