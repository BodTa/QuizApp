package huahwei_project.quizapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val questionAPI = QuestionAPIService()

    val categoriesData = MutableLiveData<List<QuestionCategory>>()
    val categoriesLoad = MutableLiveData<Boolean>()
    val categoriesError = MutableLiveData<Boolean>()

    val questionsData = MutableLiveData<List<Question>>()
    val questionsLoad = MutableLiveData<Boolean>()
    val questionsError = MutableLiveData<Boolean>()

    init {
        getCategoriesFromAPI()
    }

    fun getCategoriesFromAPI() {
        categoriesLoad.value = true

        questionAPI.getCategories().enqueue(object : Callback<List<QuestionCategory>> {
            override fun onResponse(call: Call<List<QuestionCategory>>, response: Response<List<QuestionCategory>>) {
                if (response.isSuccessful) {
                    categoriesData.value = response.body()
                    categoriesError.value = false
                } else {
                    categoriesError.value = true
                }
                categoriesLoad.value = false
            }

            override fun onFailure(call: Call<List<QuestionCategory>>, t: Throwable) {
                categoriesLoad.value = false
                categoriesError.value = true
                Log.e("RetrofitError", t.message.toString())
            }
        })
    }

    fun getCategoryQuestions(categoryId: Int, amount: Int = 10) {
        questionsLoad.value = true

        questionAPI.getQuestions(categoryId = categoryId, amount = amount).enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                if (response.isSuccessful) {
                    questionsData.value = response.body()
                    questionsError.value = false
                } else {
                    questionsError.value = true
                }
                questionsLoad.value = false
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                questionsLoad.value = false
                questionsError.value = true
                Log.e("RetrofitError", t.message.toString())
            }
        })
    }
}
