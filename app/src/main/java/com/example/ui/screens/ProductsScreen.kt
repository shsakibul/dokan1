package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.*
import com.example.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val customers by viewModel.customers.collectAsState()

    var showEditDialog by remember { mutableStateOf<Product?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Product?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Screen-level state logic
    val filteredProducts = remember(products, viewModel.productSearchQuery, viewModel.selectedCategoryFilterId, viewModel.selectedStockFilter) {
        products.filter { prod ->
            val matchesSearch = prod.name.contains(viewModel.productSearchQuery, ignoreCase = true)
            val matchesCategory = viewModel.selectedCategoryFilterId == null || prod.categoryId == viewModel.selectedCategoryFilterId
            val matchesStock = when (viewModel.selectedStockFilter) {
                "LOW_STOCK" -> prod.stock > 0.0 && prod.stock <= 3.0
                "OUT_OF_STOCK" -> prod.stock <= 0.0
                else -> true
            }
            matchesSearch && matchesCategory && matchesStock
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(Screen.AddProduct) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_product_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "নতুন পণ্য যোগ করুন")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = viewModel.productSearchQuery,
                onValueChange = { viewModel.productSearchQuery = it },
                placeholder = { Text("পণ্য অনুসন্ধান করুন...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.productSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.productSearchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("product_search_input")
            )

            // Category Horizontal Row
            Text(
                text = "ক্যাটাগরি ফিল্টার:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = viewModel.selectedCategoryFilterId == null,
                        onClick = { viewModel.selectedCategoryFilterId = null },
                        label = { Text("সব") },
                        modifier = Modifier.testTag("category_filter_all")
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = viewModel.selectedCategoryFilterId == category.id,
                        onClick = { viewModel.selectedCategoryFilterId = category.id },
                        label = { Text(category.name) },
                        modifier = Modifier.testTag("category_filter_${category.id}")
                    )
                }
            }

            // Stock filter selection chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val stockOptions = listOf(
                    "ALL" to "সব পণ্য",
                    "LOW_STOCK" to "স্বল্প স্টক",
                    "OUT_OF_STOCK" to "স্টক শেষ"
                )
                stockOptions.forEach { (id, label) ->
                    val selected = viewModel.selectedStockFilter == id
                    ElevatedFilterChip(
                        selected = selected,
                        onClick = { viewModel.selectedStockFilter = id },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            // Product List
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো পণ্য খুঁজে পাওয়া যায়নি।",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "অনুগ্রহ করে নতুন পণ্য যোগ করুন বা সঠিক ক্যাটাগরি ফিল্টার করুন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredProducts) { product ->
                        val isOutOfStock = product.stock <= 0.0
                        val isLowStock = product.stock > 0.0 && product.stock <= 3.0

                        val badgeColor = when {
                            isOutOfStock -> Color.Red
                            isLowStock -> MaterialTheme.colorScheme.secondary
                            else -> Color(0xFF2E7D32)
                        }
                        val badgeText = when {
                            isOutOfStock -> "স্টক শেষ"
                            isLowStock -> "স্বল্প স্টক"
                            else -> "পর্যাপ্ত স্টক"
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = product.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(badgeColor.copy(alpha = 0.12f))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = badgeText,
                                                    fontSize = 10.sp,
                                                    color = badgeColor,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "স্টক: ${formatBengaliStock(product.stock, product.unit)}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "ক্রয়: ${formatBengaliAmount(product.purchasePrice)} | বিক্রয়: ${formatBengaliAmount(product.salePrice)}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Quick sell button
                                        Button(
                                            onClick = { viewModel.quickSellProduct = product },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier
                                                .height(34.dp)
                                                .testTag("sell_button_${product.id}")
                                        ) {
                                            Text("বিক্রি", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Menu options
                                        var dropdownExpanded by remember { mutableStateOf(false) }
                                        Box {
                                            IconButton(onClick = { dropdownExpanded = true }) {
                                                Icon(Icons.Default.MoreVert, contentDescription = "অপশনস")
                                            }
                                            DropdownMenu(
                                                expanded = dropdownExpanded,
                                                onDismissRequest = { dropdownExpanded = false }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("নতুন স্টক যোগ করুন") },
                                                    leadingIcon = { Icon(Icons.Default.AddBusiness, contentDescription = null) },
                                                    onClick = {
                                                        viewModel.addStockProduct = product
                                                        dropdownExpanded = false
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("পণ্য সম্পাদনা") },
                                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                                    onClick = {
                                                        showEditDialog = product
                                                        dropdownExpanded = false
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("মুছে ফেলুন", color = Color.Red) },
                                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                                                    onClick = {
                                                        showDeleteDialog = product
                                                        dropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 1. Instant Quick Sell Dialog with big numeric entry and stepper
    viewModel.quickSellProduct?.let { product ->
        var sellQtyStr by remember { mutableStateOf("১") }
        var isBaki by remember { mutableStateOf(false) }
        var selectedCustomerId by remember { mutableStateOf<Int?>(null) }
        var showCustDropdown by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { viewModel.quickSellProduct = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "পণ্য বিক্রয় করুন",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "পণ্যের নাম: ${product.name}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "বর্তমান স্টক: ${formatBengaliStock(product.stock, product.unit)}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "বিক্রয়মূল্য: ${formatBengaliAmount(product.salePrice)} / ${product.unit}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quantity Stepper Selector
                    Text("পরিমাণ নির্ধারণ করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                val current = sellQtyStr.toDoubleOrNull() ?: 1.0
                                if (current > 1.0) {
                                    val newVal = (current - 1.0)
                                    sellQtyStr = if (newVal % 1.0 == 0.0) newVal.toInt().toString() else newVal.toString()
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }

                        OutlinedTextField(
                            value = sellQtyStr,
                            onValueChange = { sellQtyStr = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .width(90.dp)
                                .padding(horizontal = 8.dp)
                                .testTag("sell_quantity_input")
                        )

                        IconButton(
                            onClick = {
                                val current = sellQtyStr.toDoubleOrNull() ?: 0.0
                                val newVal = (current + 1.0)
                                sellQtyStr = if (newVal % 1.0 == 0.0) newVal.toInt().toString() else newVal.toString()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cash vs Due (নগদ বনাম বাকি)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !isBaki,
                            onClick = { isBaki = false },
                            modifier = Modifier.testTag("sell_cash_radio")
                        )
                        Text("নগদ বিক্রয়", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = isBaki,
                            onClick = { isBaki = true },
                            modifier = Modifier.testTag("sell_baki_radio")
                        )
                        Text("বাকি বিক্রয়", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // If Due/Baki, choose customer dropdown
                    if (isBaki) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            val selectedCustName = customers.find { it.id == selectedCustomerId }?.name ?: "বাকি ক্রেতা নির্বাচন করুন"
                            OutlinedCard(
                                onClick = { showCustDropdown = true },
                                modifier = Modifier.fillMaxWidth().testTag("select_baki_customer_card")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedCustName, fontSize = 13.sp)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                            DropdownMenu(
                                expanded = showCustDropdown,
                                onDismissRequest = { showCustDropdown = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                if (customers.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("কোনো ক্রেতা অ্যাকাউন্ট নেই! প্রথমে যুক্ত করুন।") },
                                        onClick = { showCustDropdown = false }
                                    )
                                } else {
                                    customers.forEach { customer ->
                                        DropdownMenuItem(
                                            text = { Text(customer.name) },
                                            onClick = {
                                                selectedCustomerId = customer.id
                                                showCustDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Confirm buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { viewModel.quickSellProduct = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                val qty = sellQtyStr.toDoubleOrNull() ?: 0.0
                                if (qty <= 0.0) {
                                    Toast.makeText(context, "সঠিক পরিমাণ লিখুন!", Toast.LENGTH_SHORT).show()
                                } else if (isBaki && selectedCustomerId == null) {
                                    Toast.makeText(context, "বাকি ক্রেতা নির্বাচন করুন!", Toast.LENGTH_SHORT).show()
                                } else {
                                    coroutineScope.launch {
                                        viewModel.sellProduct(product.id, qty, isBaki, selectedCustomerId).collect { success ->
                                            if (success) {
                                                Toast.makeText(context, "বিক্রয় সফলভাবে সম্পন্ন হয়েছে!", Toast.LENGTH_SHORT).show()
                                                viewModel.quickSellProduct = null
                                            } else {
                                                Toast.makeText(context, "বিক্রয় ব্যর্থ হয়েছে!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("confirm_sell_submit")
                        ) {
                            Text("বিক্রয় করুন")
                        }
                    }
                }
            }
        }
    }

    // 2. Add Stock Batch Dialog
    viewModel.addStockProduct?.let { product ->
        var qtyStr by remember { mutableStateOf("১০") }
        var purchasePriceStr by remember { mutableStateOf(product.purchasePrice.toInt().toString()) }
        var salePriceStr by remember { mutableStateOf(product.salePrice.toInt().toString()) }
        var supplier by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { viewModel.addStockProduct = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "নতুন স্টক যুক্ত করুন",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(text = "পণ্যের নাম: ${product.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = qtyStr,
                        onValueChange = { qtyStr = it },
                        label = { Text("নতুন স্টকের পরিমাণ (${product.unit})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                    OutlinedTextField(
                        value = purchasePriceStr,
                        onValueChange = { purchasePriceStr = it },
                        label = { Text("নতুন ক্রয়মূল্য (প্রতি ${product.unit} অনুযায়ী)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                    OutlinedTextField(
                        value = salePriceStr,
                        onValueChange = { salePriceStr = it },
                        label = { Text("নতুন বিক্রয়মূল্য (প্রতি ${product.unit} অনুযায়ী)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                    OutlinedTextField(
                        value = supplier,
                        onValueChange = { supplier = it },
                        label = { Text("সরবরাহকারী বা উৎস (ঐচ্ছিক)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { viewModel.addStockProduct = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                val qty = qtyStr.toDoubleOrNull() ?: 0.0
                                val purPrice = purchasePriceStr.toDoubleOrNull() ?: 0.0
                                val salPrice = salePriceStr.toDoubleOrNull() ?: 0.0
                                if (qty > 0.0 && purPrice > 0.0 && salPrice > 0.0) {
                                    viewModel.addStock(product.id, qty, purPrice, salPrice, supplier)
                                    viewModel.addStockProduct = null
                                    Toast.makeText(context, "স্টক সফলভাবে বৃদ্ধি পেয়েছে!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("সংরক্ষণ")
                        }
                    }
                }
            }
        }
    }

    // 3. Edit Product Dialog
    showEditDialog?.let { product ->
        var pName by remember { mutableStateOf(product.name) }
        var purchaseStr by remember { mutableStateOf(product.purchasePrice.toInt().toString()) }
        var saleStr by remember { mutableStateOf(product.salePrice.toInt().toString()) }

        Dialog(onDismissRequest = { showEditDialog = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "পণ্য তথ্য সম্পাদনা করুন",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("পণ্যের নাম") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                    OutlinedTextField(
                        value = purchaseStr,
                        onValueChange = { purchaseStr = it },
                        label = { Text("ক্রয়মূল্য") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                    OutlinedTextField(
                        value = saleStr,
                        onValueChange = { saleStr = it },
                        label = { Text("বিক্রয়মূল্য") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { showEditDialog = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                val pur = purchaseStr.toDoubleOrNull() ?: 0.0
                                val sal = saleStr.toDoubleOrNull() ?: 0.0
                                if (pName.isNotBlank() && pur > 0.0 && sal > 0.0) {
                                    viewModel.updateProduct(
                                        product.copy(name = pName, purchasePrice = pur, salePrice = sal)
                                    )
                                    showEditDialog = null
                                    Toast.makeText(context, "তথ্য সফলভাবে আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("আপডেট করুন")
                        }
                    }
                }
            }
        }
    }

    // 4. Delete Product Confirmation
    showDeleteDialog?.let { product ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("পণ্য মুছে ফেলার সতর্কতা", fontWeight = FontWeight.Bold, color = Color.Red) },
            text = { Text("আপনি কি নিশ্চিতভাবে '${product.name}' মুছে ফেলতে চান? পণ্যটি মুছে ফেললে এর সমস্ত স্টক এবং ঐতিহাসিক ডাটা সিস্টেম থেকে বাদ পড়ে যাবে।") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product)
                        showDeleteDialog = null
                        Toast.makeText(context, "পণ্যটি সফলভাবে মুছে ফেলা হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("হ্যাঁ, মুছুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()

    var name by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var purchasePrice by remember { mutableStateOf("") }
    var salePrice by remember { mutableStateOf("") }
    var initialStock by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("পিস") }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Visual onboarding header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AddBusiness,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "দোকানের নতুন পণ্য যোগ করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "নতুন পণ্যের নাম, স্টক এবং সঠিক ক্যাটাগরি নির্ধারণ করে সংরক্ষণ করুন।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("পণ্যের নাম (যেমন: মিনিকেট চাল, রূপচাঁদা তেল)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("add_product_name_input")
        )

        // Dropdown for categories
        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: "ক্যাটাগরি নির্বাচন করুন"
            OutlinedCard(
                onClick = { dropdownExpanded = true },
                modifier = Modifier.fillMaxWidth().testTag("add_product_category_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedCategoryName, fontSize = 14.sp)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text("${cat.name} (একক: ${cat.unit})") },
                        onClick = {
                            selectedCategoryId = cat.id
                            unit = cat.unit // set default unit based on selected category!
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = purchasePrice,
                onValueChange = { purchasePrice = it },
                label = { Text("ক্রয়মূল্য (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).testTag("add_product_purchase_input")
            )
            OutlinedTextField(
                value = salePrice,
                onValueChange = { salePrice = it },
                label = { Text("বিক্রয়মূল্য (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).testTag("add_product_sale_input")
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = initialStock,
                onValueChange = { initialStock = it },
                label = { Text("প্রারম্ভিক স্টক পরিমাণ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1.2f).testTag("add_product_stock_input")
            )

            // Select measurement Unit
            Box(modifier = Modifier.weight(0.8f)) {
                var expandedUnit by remember { mutableStateOf(false) }
                OutlinedCard(onClick = { expandedUnit = true }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(unit, fontSize = 13.sp)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(expanded = expandedUnit, onDismissRequest = { expandedUnit = false }) {
                    val units = listOf("কেজি", "পিস", "লিটার", "গ্রাম", "প্যাকেট", "বোতল", "ডজন")
                    units.forEach { u ->
                        DropdownMenuItem(
                            text = { Text(u) },
                            onClick = {
                                unit = u
                                expandedUnit = false
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                val catId = selectedCategoryId
                val pur = purchasePrice.toDoubleOrNull() ?: 0.0
                val sal = salePrice.toDoubleOrNull() ?: 0.0
                val stockVal = initialStock.toDoubleOrNull() ?: 0.0

                if (name.isBlank()) {
                    Toast.makeText(context, "পণ্যের নাম লিখুন!", Toast.LENGTH_SHORT).show()
                } else if (catId == null) {
                    Toast.makeText(context, "ক্যাটাগরি নির্বাচন করুন!", Toast.LENGTH_SHORT).show()
                } else if (pur <= 0.0 || sal <= 0.0) {
                    Toast.makeText(context, "ক্রয় ও বিক্রয় মূল্য সঠিক সংখ্যা দিন!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addProduct(name, catId, pur, sal, stockVal, unit)
                    Toast.makeText(context, "পণ্যটি তালিকায় সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                    viewModel.navigateBack() // Go back to products inventory
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("add_product_submit_button")
        ) {
            Text("পণ্য সংরক্ষণ করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
