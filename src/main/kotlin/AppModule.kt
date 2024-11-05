import builder.DictionaryBuilder
import converter.EnglishToJapaneseConverter
import dagger.Module
import dagger.Provides
import java.io.FileInputStream
import java.io.ObjectInputStream
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideDictionaryBuilder(): DictionaryBuilder {
        return DictionaryBuilder().apply {
            build("src/main/resources/ramen_nara.tsv")
        }
    }

    @Provides
    @Singleton
    fun provideEnglishToJapaneseConverter(): EnglishToJapaneseConverter {
        val converter = EnglishToJapaneseConverter()
        ObjectInputStream(FileInputStream("./src/main/resources/ramen_jp.dat")).use { objectInputJP ->
            ObjectInputStream(FileInputStream("./src/main/resources/ramen_en.dat")).use { objectInputEN ->
                ObjectInputStream(FileInputStream("./src/main/resources/token.dat")).use { tokenArrayObjectInput ->
                    converter.build(
                        objectInputStreamJP = objectInputJP,
                        objectInputStreamEN = objectInputEN,
                        tokenArrayObjectInputStream = tokenArrayObjectInput
                    )
                }
            }
        }
        return converter
    }
}