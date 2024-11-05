import builder.DictionaryBuilder
import converter.EnglishToJapaneseConverter
import java.io.FileInputStream
import java.io.ObjectInputStream

fun main() {
    val builder = DictionaryBuilder()
    builder.build("src/main/resources/ramen_nara.tsv")

    val objectInputJP = ObjectInputStream(FileInputStream("./src/main/resources/ramen_jp.dat"))
    val objectInputEN = ObjectInputStream(FileInputStream("./src/main/resources/ramen_en.dat"))
    val tokenArrayObjectInput = ObjectInputStream(FileInputStream("./src/main/resources/token.dat"))

    val englishToJapaneseConverter = EnglishToJapaneseConverter()
    englishToJapaneseConverter.build(
        objectInputStreamJP = objectInputJP,
        objectInputStreamEN = objectInputEN,
        tokenArrayObjectInputStream = tokenArrayObjectInput
    )
    val result = englishToJapaneseConverter.convert("Menya Eguchi")
    println(result)
}