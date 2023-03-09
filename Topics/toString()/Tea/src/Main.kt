open class Tea(private val cost: Int, private val volume: Int) {
    override fun toString(): String {
        return "cost=$cost, volume=$volume"
    }
}

class BlackTea(cost: Int, volume: Int) : Tea(cost, volume) {
    override fun toString(): String {
        return "BlackTea{${super.toString()}}"
    }
}