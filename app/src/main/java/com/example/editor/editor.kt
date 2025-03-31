package com.example.editor

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@Composable
fun Editor(viewmodel: viewmodel = viewmodel()) {
    val textElements by viewmodel.textElements.collectAsState()
    var userInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)) // Gradient Background
                )
            )
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            textElements.forEach { textElement ->
                drawContext.canvas.nativeCanvas.drawText(
                    textElement.text,
                    textElement.position.x.coerceAtLeast(5000f),
                    textElement.position.y.coerceAtLeast(5000f),
                    android.graphics.Paint().apply {
                        textSize = textElement.fontSize
                        color = android.graphics.Color.BLACK
                    }
                )
            }
        }

        textElements.forEach { textElement ->
            MoveText(textElement, viewmodel, context)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                .padding(16.dp)
                .animateContentSize()
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Enter Text", color = Color.Black) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textStyle = TextStyle(color = Color.Black)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { viewmodel.undo() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)) // Blue Button
        ) {
            Text("Undo", color = Color.White)
        }

                FloatingActionButton(
                    onClick = {
                        if (userInput.isNotEmpty()) {
                            viewmodel.addText(userInput)
                            userInput = ""
                        }
                    },

                    containerColor = Color(0xFF4CAF50) // Green FAB
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Text", tint = Color.White)
                }
        Button(
            onClick = { viewmodel.redo() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)) // Orange Button
        ) {
            Text("Redo", color = Color.White)
        }
    }


        }
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@Composable
fun MoveText(textElement: CanvasText, viewmodel: viewmodel, context: Context) {
    var offset by remember { mutableStateOf(textElement.position) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset = Offset(offset.x + dragAmount.x, offset.y + dragAmount.y)
                    viewmodel.updateTextPosition(textElement.id, offset)
                }
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                text = textElement.text,
                fontSize = textElement.fontSize.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        viewmodel.deleteText(textElement.id)
                        Toast.makeText(context, "Text deleted", Toast.LENGTH_LONG).show()
                    }
            )

            Row {
                Button(
                    onClick = { viewmodel.changeFontSize(textElement.id, increase = true) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green Button
                ) {
                    Text("+", color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewmodel.changeFontSize(textElement.id, increase = false) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)) // Red Button
                ) {
                    Text("-", color = Color.White)
                }
            }
        }
    }
}

