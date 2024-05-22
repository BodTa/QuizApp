package huahwei_project.quizapp

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuestionAPIService {
    private  val URL = "https://opentdb.com/api.php"

    private val api = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuestionAPI::class.java)

    fun GetQuestions(): Call<List<Question>>{
        return api.GetQuestions()
    }
}