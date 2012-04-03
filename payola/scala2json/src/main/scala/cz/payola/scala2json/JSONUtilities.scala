package cz.payola.scala2json

object JSONUtilities
{
    /** Returns a char escaped so that it can
      *  be used in the JSON output right away.
      *
      * @param c Char to be escaped.
      *
      * @return Escaped char.
      */
    def escapeChar(c: Char): String = {
        escapeString(c.toString)
    }

    /** Returns a string escaped and wrapped in quotes so that it can
      *  be used in the JSON output right away.
      *
      * @param str String to be escaped.
      *
      * @return Escaped string.
      */
    def escapeString(str: String): String = {
        val builder: StringBuilder = new StringBuilder

        builder.append('"')
        for (i: Int <- 0 until str.length) {
            val c: Char = str(i)
            
            c match {
                case '\\' => builder.append("\\\\")
                case '"' => builder.append("\\\"")
                case '/' => builder.append("\\/")
                case '\b' => builder.append("\\b")
                case '\f' => builder.append("\\b")
                case '\n' => builder.append("\\b")
                case '\r' => builder.append("\\b")
                case '\t' => builder.append("\\b")

                case any => builder.append(any)
            }
        }

        builder.append('"')

        builder.toString
    }
}
