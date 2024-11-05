package tsv

import kotlin.test.Test

class TsvKtTest {
    @Test
    fun readTsvFile() {
        val filePath = "src/main/resources/ramen_nara.tsv"
        val data = filePath.readTsvFile()
        data.forEach { (first, second) ->
            println("$first : $second")
        }
    }
}