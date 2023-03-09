data class Client(val name: String, val age: Int, val balance: Int) {
    override fun equals(other: Any?): Boolean {
        return other is Client && name == other.name && age == other.age
    }
}

fun main() {
    val client1 = Client(readln(), readln().toInt(), readln().toInt())
    val client2 = Client(readln(), readln().toInt(), readln().toInt())
    print(client1.equals(client2))
}