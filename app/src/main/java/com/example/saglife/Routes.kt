sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Registration : Routes("registration")
    object Forgotten : Routes("forgotten")
    object Home : Routes("home")
    object Calendar : Routes("calendar")
    object Forum : Routes("forum")
    object Map : Routes("map")
    object Profile : Routes("Profile")
    object Event : Routes("event/{id}")
    object ForumPage : Routes("forum/{id}")
}