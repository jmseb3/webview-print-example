import androidx.compose.ui.window.ComposeUIViewController
import com.wonddak.print.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
