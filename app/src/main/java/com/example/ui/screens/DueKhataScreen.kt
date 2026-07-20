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
fun DueKhataScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.customers.collectAsState()
    val totalDues = remember(customers) { customers.sumOf { it.currentDue } }

    val filteredCustomers = remember(customers, viewModel.customerSearchQuery) {
        customers.filter {
            it.name.contains(viewModel.customerSearchQuery, ignoreCase = true) ||
            it.phone.contains(viewModel.customerSearchQuery)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(Screen.AddCustomer) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_customer_fab")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "নতুন খাতা তৈরি করুন")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Hero card total market dues
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "মোট অনাদায়ী বকেয়া বা বাকি",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFD1E4FF)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatBengaliAmount(totalDues),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "মোট বাকি ক্রেতা সংখ্যা: ${convertToBanglaNumerals(customers.size.toString())} জন",
                        fontSize = 11.sp,
                        color = Color(0xFFD1E4FF).copy(alpha = 0.9f)
                    )
                }
            }

            // Customer Search Bar
            OutlinedTextField(
                value = viewModel.customerSearchQuery,
                onValueChange = { viewModel.customerSearchQuery = it },
                placeholder = { Text("নাম বা মোবাইল নম্বর দিয়ে খুঁজুন...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.customerSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.customerSearchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("customer_search_input")
            )

            // Customers list
            if (filteredCustomers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PeopleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো বাকি ক্রেতা পাওয়া যায়নি।",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "প্রয়োজনে নতুন বাকি খাতা বা অ্যাকাউন্ট তৈরি করুন।",
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
                    items(filteredCustomers) { customer ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo(Screen.CustomerDetail(customer.id)) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Custom visual circular avatar
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = customer.name.take(1),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = customer.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = customer.phone,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "বাকি:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = formatBengaliAmount(customer.currentDue),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (customer.currentDue > 0.0) Color(0xFFE65100) else Color(0xFF2E7D32)
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

@Composable
fun AddCustomerScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var nid by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Form layout Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "নতুন বাকি খাতা অ্যাকাউন্ট খুলুন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ক্রেতার নাম") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("customer_name_input")
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("customer_phone_input")
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("ঠিকানা") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = nid,
                    onValueChange = { nid = it },
                    label = { Text("জাতীয় পরিচয়পত্র নম্বর / NID (ঐচ্ছিক)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )

                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank()) {
                            Toast.makeText(context, "নাম ও মোবাইল নম্বর পূরণ করুন!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addCustomer(name, phone, address, nid.ifBlank { null })
                            Toast.makeText(context, "নতুন খাতা সফলভাবে খোলা হয়েছে!", Toast.LENGTH_SHORT).show()
                            viewModel.navigateBack()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_customer_submit_button")
                ) {
                    Text("সংরক্ষণ করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Int,
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val customer by viewModel.getCustomerById(customerId).collectAsState(initial = null)
    val transactions by viewModel.getTransactionsForCustomer(customerId).collectAsState(initial = emptyList())
    val products by viewModel.products.collectAsState()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showAddDueDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val totalPurchased = remember(transactions) {
        transactions.filter { it.type == "DUE_SALE" }.sumOf { it.amount }
    }
    val totalPaid = remember(transactions) {
        transactions.filter { it.type == "PAYMENT" }.sumOf { it.amount }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        customer?.let { cust ->
            // Customer Header Info
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cust.name.take(1),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = cust.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "ফোন: ${cust.phone}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!cust.address.isNullOrBlank()) {
                            Text(
                                text = "ঠিকানা: ${cust.address}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Ledger Stat summaries
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("মোট বাকি ক্রয়", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatBengaliAmount(totalPurchased), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("মোট পরিশোধ", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatBengaliAmount(totalPaid), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }
                Card(
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("বর্তমান বকেয়া", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text(formatBengaliAmount(cust.currentDue), fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFE65100))
                    }
                }
            }

            // Quick actions buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showAddDueDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("add_baki_sale_button")
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("বাকি যোগ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showPaymentDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("customer_pay_baki_button")
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("টাকা পরিশোধ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "বাকি খাতা লেনদেনের ইতিহাস:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Ledger Transaction list
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "এই ক্রেতার কোনো বকেয়া লেনদেন নেই।",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { tx ->
                        val isPayment = tx.type == "PAYMENT"
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isPayment) Icons.Default.CheckCircle else Icons.Default.RemoveCircle,
                                        contentDescription = null,
                                        tint = if (isPayment) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = tx.details,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = formatBengaliDate(tx.date),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = if (isPayment) "- ${formatBengaliAmount(tx.amount)}" else "+ ${formatBengaliAmount(tx.amount)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isPayment) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 1. Record Baki customer cash Payment Dialog
    if (showPaymentDialog && customer != null) {
        var payAmt by remember { mutableStateOf("") }
        var payNote by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showPaymentDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "টাকা জমা বা পরিশোধ করুন",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(text = "ক্রেতার নাম: ${customer?.name}", fontWeight = FontWeight.Bold)
                    Text(text = "সর্বমোট বকেয়া: ${formatBengaliAmount(customer?.currentDue ?: 0.0)}", color = Color.Red, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = payAmt,
                        onValueChange = { payAmt = it },
                        label = { Text("পরিশোধের পরিমাণ (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("pay_amount_input")
                    )

                    OutlinedTextField(
                        value = payNote,
                        onValueChange = { payNote = it },
                        label = { Text("মন্তব্য বা বিবরণ (ঐচ্ছিক)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { showPaymentDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                val amt = payAmt.toDoubleOrNull() ?: 0.0
                                if (amt <= 0.0) {
                                    Toast.makeText(context, "সঠিক পরিমাণ লিখুন!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.recordCustomerPayment(customerId, amt, payNote)
                                    showPaymentDialog = false
                                    Toast.makeText(context, "পরিশোধ জমা সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pay_confirm_button")
                        ) {
                            Text("জমা দিন")
                        }
                    }
                }
            }
        }
    }

    // 2. Add Due Sale Dialog
    if (showAddDueDialog && customer != null) {
        var selectedProductId by remember { mutableStateOf<Int?>(null) }
        var dueQtyStr by remember { mutableStateOf("১") }
        var dropdownExpanded by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showAddDueDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "নতুন বাকি ক্রয় যুক্ত করুন",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Product Selector Dropdown
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        val selProduct = products.find { it.id == selectedProductId }
                        val btnLabel = selProduct?.let { "${it.name} (বিক্রয়মূল্য: ${it.salePrice.toInt()}৳, স্টক: ${it.stock.toInt()})" } ?: "পণ্য নির্বাচন করুন"
                        OutlinedCard(
                            onClick = { dropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(btnLabel, fontSize = 13.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            products.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text("${p.name} (বিক্রয়: ${p.salePrice.toInt()}৳ | স্টক: ${p.stock.toInt()})") },
                                    onClick = {
                                        selectedProductId = p.id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = dueQtyStr,
                        onValueChange = { dueQtyStr = it },
                        label = { Text("ক্রয়ের পরিমাণ") },
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
                            onClick = { showAddDueDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                val pId = selectedProductId
                                val qty = dueQtyStr.toDoubleOrNull() ?: 0.0
                                if (pId == null) {
                                    Toast.makeText(context, "প্রথমে পণ্য নির্বাচন করুন!", Toast.LENGTH_SHORT).show()
                                } else if (qty <= 0.0) {
                                    Toast.makeText(context, "সঠিক পরিমাণ দিন!", Toast.LENGTH_SHORT).show()
                                } else {
                                    coroutineScope.launch {
                                        viewModel.sellProduct(pId, qty, true, customerId).collect { success ->
                                            if (success) {
                                                Toast.makeText(context, "বাকি বিক্রয় সফলভাবে লিপিবদ্ধ হয়েছে!", Toast.LENGTH_SHORT).show()
                                                showAddDueDialog = false
                                            } else {
                                                Toast.makeText(context, "ব্যর্থ হয়েছে!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাকি লিখুন")
                        }
                    }
                }
            }
        }
    }
}
