data class Student(val name: String, val money: Int) {
    override fun equals(other: Any?): Boolean {
        if (other is Student && name == other.name && other.money < 1500) return true
        return false
    }
}