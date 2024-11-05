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
        post("/calculate") {
            val input = call.receive<CalculationRequest>()
            val predictionResult = englishToJapaneseConverter.predictResultEN(input.value).map {
                englishToJapaneseConverter.convert(it)
            }
            call.respond(CalculationResponse(predictionResult))
        }
    }
}

@Serializable
data class CalculationRequest(val value: String)

@Serializable
data class CalculationResponse(val result: List<String>)