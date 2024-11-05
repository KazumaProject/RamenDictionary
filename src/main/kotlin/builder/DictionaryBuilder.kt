package builder

import louds.louds.Converter
import louds.louds.term_id.ConverterTermId
import prefix.PrefixTree
import prefix.with_term_id.PrefixTreeWithTermId
import token_array.TokenArray
import tsv.readTsvFile
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class DictionaryBuilder {
    fun build(filePath: String) {
        val dictionaryEN = PrefixTreeWithTermId()
        val dictionaryJP = PrefixTree()
        val tokenArray = TokenArray()
        val ramenStoreData = filePath.readTsvFile().sortedBy { it.first.length }
        ramenStoreData.forEach { (first, second) ->
            dictionaryEN.insert(first)
            dictionaryJP.insert(second)
        }
        val dictionaryLOUDS_EN = ConverterTermId().convert(dictionaryEN.root)
        val dictionaryLOUDS_JP = Converter().convert(dictionaryJP.root)
        dictionaryLOUDS_EN.convertListToBitSet()
        dictionaryLOUDS_JP.convertListToBitSet()

        val objectOutputEN =
            ObjectOutputStream(BufferedOutputStream(FileOutputStream("./src/main/resources/ramen_en.dat")))
        val objectOutputJP =
            ObjectOutputStream(BufferedOutputStream(FileOutputStream("./src/main/resources/ramen_jp.dat")))
        val objectOutputTokenArray = ObjectOutputStream(FileOutputStream("./src/main/resources/token.dat"))
        dictionaryLOUDS_EN.writeExternal(objectOutputEN)
        dictionaryLOUDS_JP.writeExternal(objectOutputJP)
        tokenArray.buildTokenArray(
            ramenStoreData = ramenStoreData,
            dictionaryJP = dictionaryLOUDS_JP,
            out = objectOutputTokenArray
        )
    }
}