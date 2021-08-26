package com.connect4.game

class Field (val column: Int, val row: Int) {
    var stone: Color = Color.None
        set(value) {
            val oldValue = field
            field = value
            groupList.forEach {grp -> grp.fieldColorChanged(oldValue, value)}
        }
    private val groupList = mutableListOf<Group>()

    fun addGroup (group: Group) {
        groupList.add(group)
    }

    fun isThread(color: Color): Boolean {
        return stone == Color.None && groupList.any { grp -> grp.countOfColor(color) == CONNECT_NUMBER-1}
    }
}