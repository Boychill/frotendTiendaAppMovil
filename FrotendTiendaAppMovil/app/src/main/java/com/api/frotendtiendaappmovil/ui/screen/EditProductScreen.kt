package com.api.frotendtiendaappmovil.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.api.frotendtiendaappmovil.ui.viewmodel.EditProductViewModel
import com.api.frotendtiendaappmovil.util.Dimens

@Composable
fun EditProductScreen(
    navController: NavController,
    productId: String,
    viewModel: EditProductViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri -> viewModel.onImageSelected(uri) }

    LaunchedEffect(productId) { viewModel.loadProduct(productId) }
    LaunchedEffect(viewModel.successMessage) { if (viewModel.successMessage != null) navController.popBackStack() }

    Scaffold(
        topBar = {
            Row(modifier = Modifier.padding(top = 40.dp, start = Dimens.paddingSmall), verticalAlignment = Alignment.CenterVertically) {
                BackButton(onClick = { navController.popBackStack() })
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                Text("Editar Producto", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(Dimens.paddingMedium).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val modelToShow = viewModel.newImageUri ?: viewModel.currentImageUrl
            Box(
                modifier = Modifier.size(200.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (modelToShow.toString().isNotEmpty()) AsyncImage(model = modelToShow, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.AddPhotoAlternate, null, tint = Color.Gray); Text("Cambiar foto", color = Color.Gray) }
            }

            Spacer(modifier = Modifier.height(Dimens.paddingLarge))

            ResponsiveTextField(value = viewModel.nombre, onValueChange = { viewModel.nombre = it }, label = "Nombre", leadingIcon = Icons.Default.Edit)
            Spacer(modifier = Modifier.height(Dimens.paddingMedium))
            ResponsiveTextField(value = viewModel.precio, onValueChange = { viewModel.precio = it }, label = "Precio", leadingIcon = Icons.Default.AttachMoney, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(modifier = Modifier.height(Dimens.paddingMedium))
            ResponsiveTextField(value = viewModel.stock, onValueChange = { viewModel.stock = it }, label = "Stock", leadingIcon = Icons.Default.Inventory2, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(modifier = Modifier.height(Dimens.paddingMedium))
            ResponsiveTextField(value = viewModel.categorias, onValueChange = { viewModel.categorias = it }, label = "Categorías", leadingIcon = Icons.Default.Category)

            Spacer(modifier = Modifier.height(Dimens.paddingLarge))

            if (viewModel.errorMessage != null) {
                Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(Dimens.paddingMedium))
            }

            ResponsiveButton(text = "GUARDAR CAMBIOS", isLoading = viewModel.isLoading, onClick = { viewModel.updateProduct(context) })
        }
    }
}