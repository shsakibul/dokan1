package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

// Convert numbers to Bengali digits
fun convertToBanglaNumerals(numStr: String): String {
    return numStr.map { char ->
        when (char) {
            '0' -> '০'
            '1' -> '১'
            '2' -> '২'
            '3' -> '৩'
            '4' -> '৪'
            '5' -> '৫'
            '6' -> '৬'
            '7' -> '৭'
            '8' -> '৮'
            '9' -> '৯'
            else -> char
        }
    }.joinToString("")
}

// Format double amount to Bengali currency style (৳ ১২,৫০০)
fun formatBengaliAmount(amount: Double): String {
    val formatted = String.format("%,.0f", amount)
    val banglaNumerals = convertToBanglaNumerals(formatted)
    return "৳ $banglaNumerals"
}

// Format double stock (10 কেজি, ৫ পিস)
fun formatBengaliStock(stock: Double, unit: String): String {
    val formatted = if (stock % 1.0 == 0.0) stock.toInt().toString() else String.format("%.1f", stock)
    val banglaNumerals = convertToBanglaNumerals(formatted)
    return "$banglaNumerals $unit"
}

// Convert date to localized Bengali
fun formatBengaliDate(timestamp: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = timestamp
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val month = cal.get(Calendar.MONTH)
    val year = cal.get(Calendar.YEAR)
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)

    val banglaMonths = arrayOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    val banglaMonthName = banglaMonths[month]

    val period = when (hour) {
        in 5..11 -> "সকাল"
        in 12..15 -> "দুপুর"
        in 16..18 -> "বিকাল"
        in 19..23 -> "রাত"
        else -> "ভোর"
    }

    val formattedHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
    val minuteStr = String.format("%02d", minute)

    val banglaDay = convertToBanglaNumerals(day.toString())
    val banglaYear = convertToBanglaNumerals(year.toString())
    val banglaTime = convertToBanglaNumerals("$formattedHour:$minuteStr")

    return "$banglaDay $banglaMonthName $banglaYear, $period $banglaTime"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(
    title: String,
    navigationIcon: ImageVector? = null,
    onNavigationClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (containerColor == Color.White || containerColor == MaterialTheme.colorScheme.surface)
                Color(0xFFE2E8F0)
            else
                containerColor.copy(alpha = 0.3f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = if (containerColor == Color.White || containerColor == MaterialTheme.colorScheme.surface) Color(0xFF64748B) else contentColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
