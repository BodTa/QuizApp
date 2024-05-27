package huahwei_project.quizapp

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuestionAPIService {
    private  val URL = "https://opentdb.com/"

    private val api = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuestionAPI::class.java)

    fun getQuestions(amount: Int, categoryId: Int): Call<List<Question>> {
        return api.GetQuestions(amount, categoryId)
    }

    fun getCategories(): Call<List<QuestionCategory>> {
        return api.GetCategories()
    }
}