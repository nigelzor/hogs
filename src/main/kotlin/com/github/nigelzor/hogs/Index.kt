package com.github.nigelzor.hogs

data class Index(val row: Int, val col: Int) {
    override fun toString(): String {
        return "[$row, $col]"
    }
}