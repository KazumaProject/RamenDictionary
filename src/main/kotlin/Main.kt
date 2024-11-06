import builder.DictionaryBuilder
import converter.EnglishToJapaneseConverter
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

lateinit var dictionaryBuilder: DictionaryBuilder
lateinit var englishToJapaneseConverter: EnglishToJapaneseConverter

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    val appComponent = DaggerAppComponent.create()
    dictionaryBuilder = appComponent.dictionaryBuilder
    englishToJapaneseConverter = appComponent.englishToJapaneseConverter

    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }

    routing {
        post("/henkan") {
            val input = call.receive<HenkanRequest>()
            val predictionResult = englishToJapaneseConverter.predictResultEN(input.value).map {
                Pair(it, englishToJapaneseConverter.convert(it))
            }
            call.respond(HenkanResponse(predictionResult))
        }
    }
}

@Serializable
data class HenkanRequest(val value: String)

@Serializable
data class HenkanResponse(val result: List<Pair<String, String>>)