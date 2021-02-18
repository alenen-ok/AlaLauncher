package space.alena.kominch.adapters

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, onPosition: Int): Boolean
}