package huahwei_project.quizapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QuestionAPI {
    @GET("api.php")
    fun getQuestions(
        @Query("amount") amount: Int = 10,
        @Query("categoryId") categoryId: Int
    ): Call<QuestionResponse>

    @GET("api_category.php")
    fun getCategories():Call<QuestionCategoryResponse>
}