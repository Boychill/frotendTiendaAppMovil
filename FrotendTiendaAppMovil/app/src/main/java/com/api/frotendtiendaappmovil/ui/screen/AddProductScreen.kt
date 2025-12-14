package com.api.frotendtiendaappmovil.ui.screen

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.api.frotendtiendaappmovil.ui.components.BackButton
import com.api.frotendtiendaappmovil.ui.components.ResponsiveButton
import com.api.frotendtiendaappmovil.ui.components.ResponsiveTextField
import com.api.frotendtiendaappmovil.ui.viewmodel.AddProductViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: AddProductViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showImageOptions by remember { mutableStateOf(false) }

    // 1. Launcher Galería (Photo Picker)
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.selectedImageUri = uri
    }

    // 2. Launcher Archivos (GetContent)
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.selectedImageUri = uri
    }

    // 3. Launcher Cámara (Simple Bitmap)
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            // Guardar bitmap temporalmente para obtener Uri
            val file = File(context.cacheDir, "camera_temp_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) }
            viewModel.selectedImageUri = Uri.fromFile(file)
        }
    }

    Scaffold(
        topBar = {
            Row(modifier = Modifier.padding(top = 40.dp, start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                BackButton(onClick = { navController.popBackStack() })
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo Producto", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // IMAGEN CLICKEABLE
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
                    .clickable { showImageOptions = true }, // <--- Abre el diálogo
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.selectedImageUri != null) {
                    AsyncImage(model = viewModel.selectedImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, tint = Color.Gray)
                        Text("Subir foto *", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CAMPOS DE TEXTO
            ResponsiveTextField(value = viewModel.nombre, onValueChange = { viewModel.nombre = it }, label = "Nombre", leadingIcon = Icons.Default.Edit)
            Spacer(modifier = Modifier.height(16.dp))
            ResponsiveTextField(value = viewModel.precio, onValueChange = { viewModel.precio = it }, label = "Precio", leadingIcon = Icons.Default.AttachMoney, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(modifier = Modifier.height(16.dp))
            ResponsiveTextField(value = viewModel.stock, onValueChange = { viewModel.stock = it }, label = "Stock", leadingIcon = Icons.Default.Inventory2, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(modifier = Modifier.height(16.dp))
            ResponsiveTextField(value = viewModel.categorias, onValueChange = { viewModel.categorias = it }, label = "Categorías", leadingIcon = Icons.Default.Category)

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.successMessage != null) Text(viewModel.successMessage!!, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
            if (viewModel.errorMessage != null) Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)

            Spacer(modifier = Modifier.height(16.dp))
            ResponsiveButton(text = "SUBIR PRODUCTO", isLoading = viewModel.isLoading, onClick = { viewModel.uploadProduct(context) })
        }
    }

    // DIÁLOGO DE OPCIONES
    if (showImageOptions) {
        AlertDialog(
            onDismissRequest = { showImageOptions = false },
            title = { Text("Seleccionar Imagen") },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("Galería de Fotos") },
                        leadingContent = { Icon(Icons.Default.PhotoLibrary, null) },
                        modifier = Modifier.clickable {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            showImageOptions = false
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Cámara") },
                        leadingContent = { Icon(Icons.Default.CameraAlt, null) },
                        modifier = Modifier.clickable {
                            cameraLauncher.launch()
                            showImageOptions = false
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Archivos") },
                        leadingContent = { Icon(Icons.Default.Folder, null) },
                        modifier = Modifier.clickable {
                            fileLauncher.launch("image/*")
                            showImageOptions = false
                        }
                    )
                }
            },
            confirmButton = { TextButton(onClick = { showImageOptions = false }) { Text("Cancelar") } }
        )
    }
}