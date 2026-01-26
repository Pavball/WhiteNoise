package pavball.hr.whitenoise.ui.screens.sound.select

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_moonlight
import whitenoise.composeapp.generated.resources.city
import whitenoise.composeapp.generated.resources.classic
import whitenoise.composeapp.generated.resources.countryside
// Placeholder imports - Replace these with your actual image resources (e.g. img_ocean, img_piano)
import whitenoise.composeapp.generated.resources.ic_inbox
import whitenoise.composeapp.generated.resources.ic_profile
import whitenoise.composeapp.generated.resources.icons
import whitenoise.composeapp.generated.resources.jazz
import whitenoise.composeapp.generated.resources.nature
import whitenoise.composeapp.generated.resources.ocean
import whitenoise.composeapp.generated.resources.piano
import whitenoise.composeapp.generated.resources.rain

@Composable
fun SelectSoundScreen(modifier: Modifier = Modifier) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    // Mock Data mimicking your screenshot
    // You should replace 'ic_inbox' etc. with your actual image resources (e.g. Res.drawable.img_ocean)
    val soundCategories = listOf(
        SoundCategory("classic", Res.drawable.classic),
        SoundCategory("ocean", Res.drawable.ocean),
        SoundCategory("countryside", Res.drawable.countryside),
        SoundCategory("nature", Res.drawable.nature),
        SoundCategory("jazz", Res.drawable.jazz),
        SoundCategory("piano", Res.drawable.piano),
        SoundCategory("rain", Res.drawable.rain),
        SoundCategory("city", Res.drawable.city)
    )

    // Selection State
    var selectedSound by remember { mutableStateOf("ocean") }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Layer
        Image(
            painter = painterResource(Res.drawable.bg_moonlight),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        // 3. Main Content
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Header ---
            Text(
                text = "Sounds",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(20.dp)

            // --- Grid ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp), // More vertical space for text
                modifier = Modifier.fillMaxSize()
            ) {
                items(soundCategories) { category ->
                    SoundCategoryCard(
                        name = category.name,
                        imageRes = category.imageRes,
                        isSelected = selectedSound == category.name,
                        fontFamily = quickSandFont,
                        onClick = { selectedSound = category.name }
                    )
                }
            }
        }
    }
}

@Composable
fun SoundCategoryCard(
    name: String,
    imageRes: DrawableResource,
    isSelected: Boolean,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        // Image Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Makes it a square
                .clip(RoundedCornerShape(24.dp)) // heavily rounded corners like image
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) Mint else Color.Transparent,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Optional: Light overlay on selected items to make them pop
            if (isSelected) {
                Box(modifier = Modifier.fillMaxSize().background(Mint.copy(alpha = 0.2f)))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Label
        Text(
            text = name,
            fontFamily = fontFamily,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

data class SoundCategory(
    val name: String,
    val imageRes: DrawableResource
)