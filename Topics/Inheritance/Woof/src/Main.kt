open class Dog(private val name: String, private val color: String, private val weight: Int) {
    fun printInfo() {
        println("The dog's name is $name, his color is $color and his weight is $weight")
    }
} 
// do not change the code above 
class Labrador(name: String, color: String, weight: Int) : Dog(name, color, weight)