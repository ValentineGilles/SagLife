sealed class Routes(val route: String) {
    object Login : Routes("Login")
    object Registration : Routes("registration")
    object Forgotten : Routes("forgotten")
    object Home : Routes("home")
    object Calendar : Routes("calendar")
    object Forum : Routes("forum")
    object Map : Routes("map")

}