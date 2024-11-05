package tsv

import java.io.File

fun String.readTsvFile(): MutableList<Pair<String, String>> {
    val resultList = mutableListOf<Pair<String, String>>()
    File(this).forEachLine { line ->
        val columns = line.split("\t")
        if (columns.size >= 2) {
            val firstColumn = columns[0]
            val secondColumn = columns[1]
            resultList.add(firstColumn to secondColumn)
        }
    }
    return resultList
}