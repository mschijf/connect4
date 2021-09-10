package com.connect4.game

class Field (val fieldIndex: Int) {
    var stone: Color = Color.None
        set(value) {
            val oldValue = field
            field = value
            groupList.forEach {grp -> grp.fieldColorChanged(oldValue, value)}
        }
    private val groupList = mutableListOf<Group>()

    val isOdd: Boolean = (fieldIndex / MAX_COL) % 2 == 0
    val isEven: Boolean = !isOdd

    fun addGroup (group: Group) {
        groupList.add(group)
    }

    fun isThread(color: Color): Boolean {
        return stone == Color.None && groupList.any { grp -> grp.countOfColor(color) == CONNECT_NUMBER-1}
    }

    fun isPartOfCompleteGroupOfOneColor() : Boolean {
        return stone != Color.None && groupList.any { grp -> grp.countOfColor(stone) == CONNECT_NUMBER }
    }

}