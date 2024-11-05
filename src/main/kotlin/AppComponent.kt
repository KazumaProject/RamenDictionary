import builder.DictionaryBuilder
import converter.EnglishToJapaneseConverter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    val dictionaryBuilder: DictionaryBuilder
    val englishToJapaneseConverter: EnglishToJapaneseConverter
}