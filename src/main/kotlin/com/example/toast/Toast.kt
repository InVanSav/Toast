package com.example.toast

import javafx.animation.FadeTransition
import javafx.animation.TranslateTransition
import javafx.application.Application
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.paint.Color
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.WindowEvent
import javafx.util.Duration
import kotlin.system.exitProcess


class Config {
    var alpha = 1.0
    var openTime = 7000.0
    var imageType = Toast.ImageStyle.RECTANGLE
    var title = "TITLE"
    var message = "MESSAGE"
    var appName = "APP NAME"
    var image = "IMAGE"
    var animFlag = false
    var fromX = 0.0
    var toX = 0.0
    var winX = 0.0
    var winY = 0.0
    var flagBtn = 0
    var soundFile = ""
    lateinit var player: MediaPlayer
    lateinit var button1: Button
    lateinit var button2: Button
    lateinit var eventForButton1: EventHandler<ActionEvent>
    lateinit var eventForButton2: EventHandler<ActionEvent>
    var buttonText1 = ""
    var buttonText2 = ""
}

class Toast {
    private var config = Config()
    private val windows = Stage()
    private var root = BorderPane(Group())
    private var hBox = HBox()

    enum class ImageStyle {
        CIRCLE, RECTANGLE
    }

    enum class AnimCoordinates {
        LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM
    }

    enum class Animation {
        FADE, TRANSLATE
    }

    class Builder {
        private var config = Config()

        fun setTitle(str: String): Builder {
            config.title = str
            return this
        }

        fun setMessage(str: String): Builder {
            config.message = str
            return this
        }

        fun setAppName(str: String): Builder {
            config.appName = str
            return this
        }

        fun setImage(src: String, type: ImageStyle): Builder {
            config.image = src
            config.imageType = type

            return this
        }

        fun setAnim(anim: Animation): Builder {
            when (anim) {
                Animation.FADE -> config.animFlag = true
                Animation.TRANSLATE -> config.animFlag = false
            }

            return this
        }

        fun setAnimCoordinates(coordinates: AnimCoordinates): Builder {
            if (config.flagBtn != 0) {
                when (coordinates) {
                    AnimCoordinates.LEFT_TOP -> setXYCoordinates(-400.0, 0.0, 100.0)
                    AnimCoordinates.RIGHT_TOP -> setXYCoordinates(400.0, 895.0, 100.0)
                    AnimCoordinates.LEFT_BOTTOM -> setXYCoordinates(-400.0, 0.0, 500.0)
                    AnimCoordinates.RIGHT_BOTTOM -> setXYCoordinates(400.0, 895.0, 500.0)
                }
            } else {
                when (coordinates) {
                    AnimCoordinates.LEFT_TOP -> setXYCoordinates(-320.0, 0.0, 100.0)
                    AnimCoordinates.RIGHT_TOP -> setXYCoordinates(320.0, 960.0, 100.0)
                    AnimCoordinates.LEFT_BOTTOM -> setXYCoordinates(-320.0, 0.0, 500.0)
                    AnimCoordinates.RIGHT_BOTTOM -> setXYCoordinates(320.0, 960.0, 500.0)
                }
            }

            return this
        }

        private fun setXYCoordinates(fromX: Double, winX: Double, winY: Double) {
            config.fromX = fromX
            config.winX = winX
            config.winY = winY
        }

        fun setButtons(count: Int,
                       btnStr_1: String,
                       btnStr_2: String,
                       event1: EventHandler<ActionEvent>,
                       event2: EventHandler<ActionEvent>): Builder
        {
            when (count) {
                1 -> {
                    config.flagBtn = count

                    config.buttonText1 = btnStr_1
                    config.button1 = Button(btnStr_1)

                    config.eventForButton1 = event1
                } 2 -> {
                    config.flagBtn = count

                    config.buttonText1 = btnStr_1
                    config.buttonText2 = btnStr_2

                    config.button1 = Button(btnStr_1)
                    config.button2 = Button(btnStr_2)

                    config.eventForButton1 = event1
                    config.eventForButton2 = event2
                }
            }

            return this
        }

        fun setSoundEvent(str: String): Builder {
            config.soundFile = str
            return this
        }

        fun build(): Toast  {
            val toast = Toast()
            toast.config = config
            toast.build()

            return toast
        }
    }

    private fun build() {
        windows.initStyle(StageStyle.TRANSPARENT)
        windows.isResizable = true

        windows.x = config.winX
        windows.y = config.winY

        windows.scene = Scene(root, windows.width, windows.height)
        windows.scene.fill = Color.TRANSPARENT

        imageFigure()

        val vbox = VBox()

        val title = Label(config.title)
        val message = Label(config.message)
        val appName = Label(config.appName)
        vbox.children.addAll(title, message, appName)

        val vBoxBtn = VBox()
        val hBoxBnt1 = HBox()
        val hBoxBtn2 = HBox()

        if (config.flagBtn == 1) {
            val button = config.button1
            button.onAction = config.eventForButton1

            hBoxBnt1.children.add(button)
            vBoxBtn.children.add(hBoxBnt1)

            button.style = "-fx-background-color: #D3D3D3;" +
                    "-fx-background-radius: 5px;" +
                    "-fx-font-size: 11pt;" +
                    "-fx-padding: 5px 10px;"

        } else if (config.flagBtn == 2) {
            val button1 = config.button1
            button1.onAction = config.eventForButton1

            val button2 = config.button2
            button2.onAction = config.eventForButton2

            hBoxBnt1.children.add(button1)
            hBoxBtn2.children.add(button2)

            vBoxBtn.children.addAll(hBoxBnt1, hBoxBtn2)

            config.button1.style = "-fx-background-color: #D3D3D3;" +
                    "-fx-background-radius: 5px;" +
                    "-fx-font-size: 11pt;" +
                    "-fx-padding: 5px 10px;"

            config.button2.style = "-fx-background-color: #D3D3D3;" +
                    "-fx-background-radius: 5px;" +
                    "-fx-font-size: 11pt;" +
                    "-fx-padding: 5px 10px;"

        }

        val sound = Media(config.soundFile)
        val player = MediaPlayer(sound)
        config.player = player

        title.style = "-fx-text-fill: #F08080;" +
                "-fx-font-size: 14pt;" +
                "-fx-underline: true;"

        root.style = "-fx-background-color: #E0FFFF;" +
                "-fx-background-radius: 15px;" +
                "-fx-border-radius: 15px;" +
                "-fx-font-size: 12pt;" +
                "-fx-font-family: Comic Sans MS, Comic Sans, cursive;" +
                "-fx-padding: 20px;" +
                "-fx-line-spacing: 1.5"

        vbox.style = "-fx-padding: 15px 10px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);"

        vBoxBtn.style = "-fx-background: #FFDAB9;" +
                "-fx-padding: 15px 10px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);"

        hBoxBnt1.style = "-fx-padding: 5px;"

        hBoxBtn2.style = "-fx-padding: 5px;"

        hBox.style ="-fx-padding: 5px;"

        hBox.children.addAll(vbox, vBoxBtn)
        root.center = hBox
    }

    private fun imageFigure() {
        if (config.image.isEmpty()) {
            return
        }

        val iconBorder =
            if (config.imageType == ImageStyle.RECTANGLE) {
                Rectangle(100.0, 100.0)
            }  else {
                Circle(50.0, 50.0, 50.0)
            }

        iconBorder.fill = ImagePattern(Image(config.image))
        hBox.children.add(iconBorder)
    }

    private fun fadeOpenAnimation() {
        val anim = FadeTransition(Duration.millis(1500.0), root)
        anim.fromValue = 0.0
        anim.toValue = config.alpha
        anim.cycleCount = 1

        if (config.soundFile.isNotEmpty()) {
            config.player.play()
        }

        anim.play()
    }

    private fun fadeCloseAnimation() {
        val anim = FadeTransition(Duration.millis(1500.0), root)
        anim.fromValue = config.alpha
        anim.toValue = 0.0
        anim.cycleCount = 1
        anim.onFinished = EventHandler {
            Platform.exit()
            exitProcess(0)
        }
        anim.play()
    }

    private fun transOpenAnimation() {
        val anim = TranslateTransition(Duration.millis(1500.0), root)
        anim.fromX = config.fromX
        anim.toX = config.toX
        anim.cycleCount = 1

        if (config.soundFile.isNotEmpty()) {
            config.player.play()
        }

        anim.play()
    }

    private fun transCloseAnimation() {
        val anim = TranslateTransition(Duration.millis(1500.0), root)
        anim.fromX = config.toX
        anim.toX = config.fromX
        anim.cycleCount = 1
        anim.onFinished = EventHandler {
            Platform.exit()
            exitProcess(0)
        }
        anim.play()
    }

    fun start() {
        windows.show()

        if (config.animFlag) {
            fadeOpenAnimation()
            val thread = Thread {
                try {
                    Thread.sleep(config.openTime.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                fadeCloseAnimation()
            }
            Thread(thread).start()
        } else {
            transOpenAnimation()
            val thread = Thread {
                try {
                    Thread.sleep(config.openTime.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                transCloseAnimation()
            }
            Thread(thread).start()
        }
    }

}


class SomeClass: Application() {
    override fun start(p0: Stage?) {
        val toast = Toast.Builder()
            .setTitle("!!!CookKing!!!")
            .setMessage("Recipe of The Day!")
            .setAppName("Soup with tomato")
            .setImage(
                "https://images.unsplash.com/photo-1534939561126-855b8675edd7?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MTJ8fHNvdXB8ZW58MHx8MHx8&auto=format&fit=crop&w=500&q=60",
                Toast.ImageStyle.RECTANGLE
            )
            .setAnim(Toast.Animation.TRANSLATE)
            .setButtons(2, "Hello!", "ByBy!", {}, {})
            .setAnimCoordinates(Toast.AnimCoordinates.RIGHT_TOP)
            .setSoundEvent("https://audiokaif.ru/wp-content/uploads/2022/02/1-%D0%97%D0%B2%D1%83%D0%BA-%D0%B2%D0%BA%D0%BB%D1%8E%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-Macbook-1.mp3")
            .build()
        toast.start()
    }
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(SomeClass::class.java)
        }
    }
}