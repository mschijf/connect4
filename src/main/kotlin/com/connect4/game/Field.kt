package com.connect4.game

class Field (val column: Int, val row: Int) {
    var stone: Color = Color.None
    val groupList = mutableListOf<Group>()


    fun addGroup (group: Group) {
        groupList.add(group)
    }
}