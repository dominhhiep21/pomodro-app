package thong.kotlin.pomodoro.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object FocusRuleIcons {

    val TaskRequired: ImageVector
        get() {
            if (_taskRequired != null) return _taskRequired!!

            _taskRequired = ImageVector.Builder(
                name = "TaskRequired",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(8f, 5f)
                    horizontalLineTo(6.8f)
                    curveTo(5.8f, 5f, 5f, 5.8f, 5f, 6.8f)
                    verticalLineTo(20.2f)
                    curveTo(5f, 21.2f, 5.8f, 22f, 6.8f, 22f)
                    horizontalLineTo(17.2f)
                    curveTo(18.2f, 22f, 19f, 21.2f, 19f, 20.2f)
                    verticalLineTo(6.8f)
                    curveTo(19f, 5.8f, 18.2f, 5f, 17.2f, 5f)
                    horizontalLineTo(16f)

                    moveTo(9f, 5f)
                    curveTo(9f, 3.9f, 9.9f, 3f, 11f, 3f)
                    horizontalLineTo(13f)
                    curveTo(14.1f, 3f, 15f, 3.9f, 15f, 5f)
                    verticalLineTo(6.5f)
                    horizontalLineTo(9f)
                    verticalLineTo(5f)

                    moveTo(8.2f, 12.1f)
                    lineTo(10f, 13.9f)
                    lineTo(13.8f, 10.1f)

                    moveTo(15f, 12f)
                    horizontalLineTo(16.5f)

                    moveTo(8.2f, 17f)
                    horizontalLineTo(16.5f)
                }
            }.build()

            return _taskRequired!!
        }

    val SingleActiveTask: ImageVector
        get() {
            if (_singleActiveTask != null) return _singleActiveTask!!

            _singleActiveTask = ImageVector.Builder(
                name = "SingleActiveTask",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(12f, 7f)
                    curveTo(9.2f, 7f, 7f, 9.2f, 7f, 12f)
                    curveTo(7f, 14.8f, 9.2f, 17f, 12f, 17f)
                    curveTo(14.8f, 17f, 17f, 14.8f, 17f, 12f)
                    curveTo(17f, 9.2f, 14.8f, 7f, 12f, 7f)
                    close()

                    moveTo(12f, 10.2f)
                    curveTo(11f, 10.2f, 10.2f, 11f, 10.2f, 12f)
                    curveTo(10.2f, 13f, 11f, 13.8f, 12f, 13.8f)
                    curveTo(13f, 13.8f, 13.8f, 13f, 13.8f, 12f)
                    curveTo(13.8f, 11f, 13f, 10.2f, 12f, 10.2f)
                    close()

                    moveTo(4.5f, 9f)
                    verticalLineTo(6.5f)
                    curveTo(4.5f, 5.4f, 5.4f, 4.5f, 6.5f, 4.5f)
                    horizontalLineTo(9f)

                    moveTo(15f, 4.5f)
                    horizontalLineTo(17.5f)
                    curveTo(18.6f, 4.5f, 19.5f, 5.4f, 19.5f, 6.5f)
                    verticalLineTo(9f)

                    moveTo(19.5f, 15f)
                    verticalLineTo(17.5f)
                    curveTo(19.5f, 18.6f, 18.6f, 19.5f, 17.5f, 19.5f)
                    horizontalLineTo(15f)

                    moveTo(9f, 19.5f)
                    horizontalLineTo(6.5f)
                    curveTo(5.4f, 19.5f, 4.5f, 18.6f, 4.5f, 17.5f)
                    verticalLineTo(15f)
                }
            }.build()

            return _singleActiveTask!!
        }

    val SkipWork: ImageVector
        get() {
            if (_skipWork != null) return _skipWork!!

            _skipWork = ImageVector.Builder(
                name = "SkipWork",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(5f, 6.8f)
                    curveTo(5f, 6f, 5.9f, 5.5f, 6.6f, 6f)
                    lineTo(13f, 11f)
                    curveTo(13.6f, 11.5f, 13.6f, 12.5f, 13f, 13f)
                    lineTo(6.6f, 18f)
                    curveTo(5.9f, 18.5f, 5f, 18f, 5f, 17.2f)
                    verticalLineTo(6.8f)
                    close()

                    moveTo(12f, 6.8f)
                    curveTo(12f, 6f, 12.9f, 5.5f, 13.6f, 6f)
                    lineTo(20f, 11f)
                    curveTo(20.6f, 11.5f, 20.6f, 12.5f, 20f, 13f)
                    lineTo(13.6f, 18f)
                    curveTo(12.9f, 18.5f, 12f, 18f, 12f, 17.2f)
                    verticalLineTo(6.8f)
                    close()

                    moveTo(21f, 6f)
                    curveTo(21.6f, 6f, 22f, 6.4f, 22f, 7f)
                    verticalLineTo(17f)
                    curveTo(22f, 17.6f, 21.6f, 18f, 21f, 18f)
                    curveTo(20.4f, 18f, 20f, 17.6f, 20f, 17f)
                    verticalLineTo(7f)
                    curveTo(20f, 6.4f, 20.4f, 6f, 21f, 6f)
                    close()
                }
            }.build()

            return _skipWork!!
        }

    val SplitTask: ImageVector
        get() {
            if (_splitTask != null) return _splitTask!!

            _splitTask = ImageVector.Builder(
                name = "SplitTask",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(12f, 8f)
                    verticalLineTo(11f)
                    curveTo(12f, 12.1f, 11.1f, 13f, 10f, 13f)
                    horizontalLineTo(8f)
                    curveTo(6.9f, 13f, 6f, 13.9f, 6f, 15f)
                    verticalLineTo(16f)

                    moveTo(12f, 11f)
                    curveTo(12f, 12.1f, 12.9f, 13f, 14f, 13f)
                    horizontalLineTo(16f)
                    curveTo(17.1f, 13f, 18f, 13.9f, 18f, 15f)
                    verticalLineTo(16f)
                }

                path(fill = SolidColor(Color.Black)) {
                    moveTo(9.5f, 3f)
                    horizontalLineTo(14.5f)
                    curveTo(15.3f, 3f, 16f, 3.7f, 16f, 4.5f)
                    verticalLineTo(7.5f)
                    curveTo(16f, 8.3f, 15.3f, 9f, 14.5f, 9f)
                    horizontalLineTo(9.5f)
                    curveTo(8.7f, 9f, 8f, 8.3f, 8f, 7.5f)
                    verticalLineTo(4.5f)
                    curveTo(8f, 3.7f, 8.7f, 3f, 9.5f, 3f)
                    close()

                    moveTo(3.5f, 16f)
                    horizontalLineTo(8.5f)
                    curveTo(9.3f, 16f, 10f, 16.7f, 10f, 17.5f)
                    verticalLineTo(20.5f)
                    curveTo(10f, 21.3f, 9.3f, 22f, 8.5f, 22f)
                    horizontalLineTo(3.5f)
                    curveTo(2.7f, 22f, 2f, 21.3f, 2f, 20.5f)
                    verticalLineTo(17.5f)
                    curveTo(2f, 16.7f, 2.7f, 16f, 3.5f, 16f)
                    close()

                    moveTo(15.5f, 16f)
                    horizontalLineTo(20.5f)
                    curveTo(21.3f, 16f, 22f, 16.7f, 22f, 17.5f)
                    verticalLineTo(20.5f)
                    curveTo(22f, 21.3f, 21.3f, 22f, 20.5f, 22f)
                    horizontalLineTo(15.5f)
                    curveTo(14.7f, 22f, 14f, 21.3f, 14f, 20.5f)
                    verticalLineTo(17.5f)
                    curveTo(14f, 16.7f, 14.7f, 16f, 15.5f, 16f)
                    close()
                }
            }.build()

            return _splitTask!!
        }

    val LearningJournal: ImageVector
        get() {
            if (_learningJournal != null) return _learningJournal!!

            _learningJournal = ImageVector.Builder(
                name = "LearningJournal",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(7f, 4f)
                    horizontalLineTo(18f)
                    curveTo(19.1f, 4f, 20f, 4.9f, 20f, 6f)
                    verticalLineTo(20f)
                    curveTo(20f, 21.1f, 19.1f, 22f, 18f, 22f)
                    horizontalLineTo(7f)
                    curveTo(5.3f, 22f, 4f, 20.7f, 4f, 19f)
                    verticalLineTo(7f)
                    curveTo(4f, 5.3f, 5.3f, 4f, 7f, 4f)
                    close()

                    moveTo(8f, 4f)
                    verticalLineTo(22f)

                    moveTo(11f, 9f)
                    horizontalLineTo(17f)

                    moveTo(11f, 13f)
                    horizontalLineTo(17f)

                    moveTo(11f, 17f)
                    horizontalLineTo(15f)

                    moveTo(5f, 8f)
                    horizontalLineTo(7f)

                    moveTo(5f, 12f)
                    horizontalLineTo(7f)

                    moveTo(5f, 16f)
                    horizontalLineTo(7f)
                }
            }.build()

            return _learningJournal!!
        }

    val WarningFocus: ImageVector
        get() {
            if (_warningFocus != null) return _warningFocus!!

            _warningFocus = ImageVector.Builder(
                name = "WarningFocus",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.9f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(12f, 3.5f)
                    lineTo(22f, 20.5f)
                    horizontalLineTo(2f)
                    lineTo(12f, 3.5f)
                    close()

                    moveTo(12f, 9f)
                    verticalLineTo(14f)

                    moveTo(12f, 17.3f)
                    lineTo(12.01f, 17.3f)
                }
            }.build()

            return _warningFocus!!
        }

    private var _taskRequired: ImageVector? = null
    private var _singleActiveTask: ImageVector? = null
    private var _skipWork: ImageVector? = null
    private var _splitTask: ImageVector? = null
    private var _learningJournal: ImageVector? = null
    private var _warningFocus: ImageVector? = null
}