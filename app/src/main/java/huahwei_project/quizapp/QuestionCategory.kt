package huahwei_project.quizapp

data class QuestionCategory(val id:Int, val name:String)

data class QuestionCategoryResponse(val trivia_categories:List<QuestionCategory>)