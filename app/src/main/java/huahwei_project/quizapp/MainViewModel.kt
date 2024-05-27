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
}
