package huahwei_project.quizapp

import retrofit2.Call
import retrofit2.http.GET

interface QuestionAPI {
    @GET("api.php")
    fun GetQuestions(
        @Query("amount") amount: Int = 10,
        @Query("categoryId") categoryId: Int
    ): Call<List<Question>>

    @GET("api_category.php")
    fun GetCategories():Call<List<QuestionCategory>>
}