package com.example.editor

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class viewmodel : ViewModel(){
    private val _textElements = MutableStateFlow<List<CanvasText>>(emptyList())
    val textElements: StateFlow<List<CanvasText>> = _textElements

    private val undoStack = mutableListOf<List<CanvasText>>()
    private val redoStack = mutableListOf<List<CanvasText>>()


    private fun saveStateForUndo(){
        undoStack.add(_textElements.value.map{it.copy()})
        redoStack.clear()
    }

    fun addText(text: String){
        saveStateForUndo()
        val newText = CanvasText(
            id = (_textElements.value.maxByOrNull { it.id }?.id ?: 0) + 1,
            text = text,
            fontSize = 16f,
            position = Offset(100f,100f)
        )
        _textElements.value = _textElements.value + newText
    }

    fun updateTextPosition(id: Int, position: Offset) {
        saveStateForUndo()
        _textElements.value = _textElements.value.map {
            if (it.id == id) {
                it.copy(position = position)
            } else {
                it
            }
        }
    }

    fun changeFontSize(id: Int, increase : Boolean) {
        saveStateForUndo()
        _textElements.value = _textElements.value.map {
            if(it.id == id){
                it.copy(fontSize = it.fontSize + if(increase) 2f else -2f)
            }
            else{
                it
            }
        }
    }

    fun deleteText(id : Int){
        saveStateForUndo()
        _textElements.value = _textElements.value.filter { it.id != id }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun undo(){
        if(undoStack.isNotEmpty()){
            redoStack.add(_textElements.value.map{it.copy()})
            _textElements.value = undoStack.removeLast()
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun redo(){
        if(redoStack.isNotEmpty()){
            undoStack.add(_textElements.value.map{it.copy()})
            _textElements.value = redoStack.removeLast()
        }
    }

}

data class CanvasText(
    val id: Int,
    var text: String,
    var fontSize: Float,
    var position: Offset
)