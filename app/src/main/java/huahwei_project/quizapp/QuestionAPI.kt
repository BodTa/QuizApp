package huahwei_project.quizapp

import retrofit2.Call
import retrofit2.http.GET

interface QuestionAPI {
    @GET("?amount=20")
    fun GetQuestions():Call<List<Question>>
}