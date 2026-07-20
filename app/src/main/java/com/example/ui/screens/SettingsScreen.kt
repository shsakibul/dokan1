package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.*
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    var shopName by remember { mutableStateOf("হাসান ভাইয়ের মুদির দোকান") }
    var shopPhone by remember { mutableStateOf("০১৭১১-২২৩৩৪৪") }
    var shopAddress by remember { mutableStateOf("মিরপুর-১০, ঢাকা") }

    var showAnalysisDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shop Profile Edit Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "দোকানের তথ্য পরিবর্তন করুন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("দোকানের নাম") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = shopPhone,
                    onValueChange = { shopPhone = it },
                    label = { Text("মোবাইল নম্বর") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = shopAddress,
                    onValueChange = { shopAddress = it },
                    label = { Text("ঠিকানা") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                )

                Button(
                    onClick = {
                        Toast.makeText(context, "দোকানের প্রোফাইল তথ্য সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("তথ্য সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Smart Analysis card launcher
        Card(
            onClick = { showAnalysisDialog = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth().testTag("launch_smart_analysis")
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "স্মার্ট ব্যবসায়িক বিশ্লেষণ ও স্টক অনুমান",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "কোন পণ্য বেশি বিক্রি হয় এবং কখন স্টক লাগবে দেখুন।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Category Management button
        Card(
            onClick = { viewModel.navigateTo(Screen.CategoryManagement) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("launch_category_management")
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ক্যাটাগরি ও একক ব্যবস্থাপনা",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "নতুন ক্যাটাগরি তৈরি করুন ও পণ্য একক পরিচালনা করুন।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Security & Simulating Data backups
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "নিরাপত্তা ও ডেটা ব্যাকআপ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(context, "ক্লাউড ব্যাকআপ সফলভাবে সম্পন্ন হয়েছে!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("ক্লাউড ব্যাকআপ (Google Drive)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("আপনার সম্পূর্ণ হিসাব ব্যাকআপ রাখুন যাতে হারিয়ে না যায়।", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Divider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(context, "পিন কোড লক সফলভাবে সক্রিয় করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("সিকিউরিটি পিন লক (PIN Lock)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("অ্যাপটি সুরক্ষিত করতে ৪ সংখ্যার পাসকোড সক্রিয় করুন।", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    // Smart Analysis Report Sheet/Dialog
    if (showAnalysisDialog) {
        val analysis by viewModel.businessAnalysis.collectAsState()

        Dialog(onDismissRequest = { showAnalysisDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "স্মার্ট ব্যবসায়িক বিশ্লেষণ রিপোর্ট",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Best and Low sales
                    Text("পণ্য বিক্রয় প্রবণতা (গত ৭ দিন):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("সবচেয়ে বেশি বিক্রি হওয়া পণ্য:", fontSize = 11.sp, color = Color(0xFF2E7D32))
                                Text(
                                    text = if (analysis.bestSellingQty > 0.0) "${analysis.bestSellingProduct} — ${formatBengaliStock(analysis.bestSellingQty, analysis.bestSellingUnit)}" else "কোনো ডাটা নেই",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFC62828))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("সবচেয়ে কম বিক্রি হওয়া পণ্য:", fontSize = 11.sp, color = Color(0xFFC62828))
                                Text(
                                    text = if (analysis.lowSellingQty > 0.0) "${analysis.lowSellingProduct} — ${formatBengaliStock(analysis.lowSellingQty, analysis.lowSellingUnit)}" else "কোনো ডাটা নেই",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Demand Predictions and Advice
                    Text("স্টক সতর্কতা ও চাহিদা অনুমান:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    if (analysis.predictions.isEmpty()) {
                        Text(
                            text = "বর্তমানে স্টক ফুরিয়ে যাওয়ার মতো কোনো পণ্য নেই। সব পণ্য পর্যাপ্ত মজুদ রয়েছে!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        analysis.predictions.forEach { pred ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${pred.productName} ফুরিয়ে যাবে!",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = pred.advice,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Business tips card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("💡 পরামর্শ:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "বেশি বিক্রি হওয়া পণ্যগুলোর স্টক সবসময় সচল রাখুন। স্টক শেষ হতে ৩ দিন বাকি থাকতেই পুনরায় নতুন স্টক সংগ্রহ করা লাভজনক ব্যবসার জন্য অত্যন্ত সহায়ক।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showAnalysisDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("বন্ধ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var categoryUnit by remember { mutableStateOf("পিস") }

    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_category_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "নতুন ক্যাটাগরি")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "দোকানের ক্যাটাগরি তালিকা:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = cat.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "বিক্রয়ের একক: ${cat.unit}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            // Don't delete built-in seeded categories to keep app state safe, but allow deleting custom added ones
                            if (cat.id > 11) {
                                IconButton(
                                    onClick = {
                                        viewModel.deleteCategory(cat)
                                        Toast.makeText(context, "ক্যাটাগরি মুছে ফেলা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "মুছুন", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "নতুন ক্যাটাগরি তৈরি করুন",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("ক্যাটাগরির নাম (যেমন: মশলা, জুস)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("add_category_name_input")
                    )

                    // Select measurement Unit
                    Text("বিক্রয়ের একক নির্ধারণ করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var unitDropdown by remember { mutableStateOf(false) }
                        OutlinedCard(onClick = { unitDropdown = true }, modifier = Modifier.fillMaxWidth().testTag("add_category_unit_card")) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(categoryUnit, fontSize = 13.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(expanded = unitDropdown, onDismissRequest = { unitDropdown = false }) {
                            val units = listOf("পিস", "কেজি", "লিটার", "গ্রাম", "প্যাকেট", "বোতল", "জোড়া", "ডজন")
                            units.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text(u) },
                                    onClick = {
                                        categoryUnit = u
                                        unitDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { showAddDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                if (categoryName.isNotBlank()) {
                                    viewModel.addCategory(categoryName, categoryUnit)
                                    categoryName = ""
                                    showAddDialog = false
                                    Toast.makeText(context, "নতুন ক্যাটাগরি সফলভাবে তৈরি হয়েছে!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_category_submit_button")
                        ) {
                            Text("তৈরি করুন")
                        }
                    }
                }
            }
        }
    }
}
