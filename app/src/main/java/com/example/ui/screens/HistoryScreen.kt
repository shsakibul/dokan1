package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.*
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    var historySearchQuery by remember { mutableStateOf("") }

    val filteredTransactions = remember(transactions, historySearchQuery, viewModel.historyTypeFilter) {
        transactions.filter { tx ->
            val matchesSearch = tx.details.contains(historySearchQuery, ignoreCase = true)
            val matchesType = viewModel.historyTypeFilter == null || tx.type == viewModel.historyTypeFilter
            matchesSearch && matchesType
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search filter input
        OutlinedTextField(
            value = historySearchQuery,
            onValueChange = { historySearchQuery = it },
            placeholder = { Text("লেনদেনের বিবরণ দিয়ে খুঁজুন...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (historySearchQuery.isNotEmpty()) {
                    IconButton(onClick = { historySearchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("history_search_input")
        )

        // Type Filter LazyRow
        Text(
            text = "লেনদেনের ধরন নির্বাচন করুন:",
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
            val filters = listOf(
                null to "সব লেনদেন",
                "SALE" to "নগদ বিক্রয়",
                "DUE_SALE" to "বাকি বিক্রয়",
                "PAYMENT" to "টাকা পরিশোধ",
                "STOCK_BUY" to "স্টক ক্রয়",
                "EXPENSE" to "অন্যান্য ব্যয়"
            )
            items(filters) { (type, label) ->
                val selected = viewModel.historyTypeFilter == type
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.historyTypeFilter = type },
                    label = { Text(label, fontSize = 11.sp) }
                )
            }
        }

        // Ledger list
        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "কোনো লেনদেনের তথ্য পাওয়া যায়নি।",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredTransactions) { tx ->
                    val colorTheme = when (tx.type) {
                        "SALE" -> Color(0xFF2E7D32) // green
                        "DUE_SALE" -> Color(0xFFE65100) // orange
                        "PAYMENT" -> Color(0xFF00796B) // teal
                        "STOCK_BUY" -> Color(0xFFC62828) // red
                        "EXPENSE" -> Color(0xFFD32F2F) // dark red
                        else -> MaterialTheme.colorScheme.primary
                    }

                    val prefix = when (tx.type) {
                        "SALE", "DUE_SALE", "PAYMENT" -> "+"
                        "STOCK_BUY", "EXPENSE" -> "-"
                        else -> ""
                    }

                    val typeLabel = when (tx.type) {
                        "SALE" -> "নগদ বিক্রয়"
                        "DUE_SALE" -> "বাকি বিক্রয়"
                        "PAYMENT" -> "টাকা পরিশোধ"
                        "STOCK_BUY" -> "স্টক ক্রয়"
                        "EXPENSE" -> "অন্যান্য ব্যয়"
                        else -> ""
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Circular icon representing the cash flow direction
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(colorTheme.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (tx.type) {
                                            "SALE" -> Icons.Default.ArrowUpward
                                            "DUE_SALE" -> Icons.Default.HourglassEmpty
                                            "PAYMENT" -> Icons.Default.CheckCircle
                                            "STOCK_BUY" -> Icons.Default.AddBusiness
                                            "EXPENSE" -> Icons.Default.Receipt
                                            else -> Icons.Default.Info
                                        },
                                        contentDescription = null,
                                        tint = colorTheme,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = tx.details,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(colorTheme.copy(alpha = 0.12f))
                                                .padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = typeLabel,
                                                fontSize = 9.sp,
                                                color = colorTheme,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = formatBengaliDate(tx.date),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "$prefix ${formatBengaliAmount(tx.amount)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = colorTheme
                            )
                        }
                    }
                }
            }
        }
    }
}
