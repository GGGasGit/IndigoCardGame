open class Car(model: String, speed: Int)

class Bus(private val typeOfBus: String, private val model: String, private val speed: Int) : Car(model, speed) {
    fun printInfo() = println("Type of bus: $typeOfBus, model: $model, speed: $speed")
}

fun main() {
    val bus = Bus("Personal", "N4", 130)
    bus.printInfo()
}