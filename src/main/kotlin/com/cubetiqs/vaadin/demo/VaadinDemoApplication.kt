package com.cubetiqs.vaadin.demo

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

@SpringBootApplication
class VaadinDemoApplication

fun main(args: Array<String>) {
    runApplication<VaadinDemoApplication>(*args)
}

@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover")
@Push
@Theme(Lumo::class)
open class MainLayout : AppLayout()

@Route(value = "", layout = MainLayout::class)
@PageTitle("Home")
class HomeView : Div() {
    private var thread: Thread? = null
    private val timeLeft: AtomicLong = AtomicLong(60)
    private val title = Text("")
	private val radioGroup = RadioButtonGroup<String>().apply {
		addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
		addValueChangeListener {
			println("Selected item: ${it.value}")
		}
	}

	private val items: MutableList<String> = mutableListOf(
		"Option A",
		"Option B",
		"Option C",
		"Option D",
	)

    init {
        val container = HorizontalLayout().apply {
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        }

        val form = FormLayout()
		form.add(title)
		radioGroup.setItems(items)
		form.add(radioGroup)

        val nameField = TextField("Enter your name")
        form.add(nameField)
        form.add(Button("Click me") {
            Notification.show("Hi, ${nameField.value ?: "guy"}!")
        })

        container.add(form)
        add(container)
    }

    override fun onAttach(attachEvent: AttachEvent) {
        super.onAttach(attachEvent)

        ui.ifPresent {
            thread = Thread {
                while (timeLeft.get() != 0L) {
                    it.access {
                        title.text = "Time left: ${timeLeft.get()}s"
                    }
					Thread.sleep(TimeUnit.SECONDS.toMillis(1))
					timeLeft.decrementAndGet()
                }

				thread?.interrupt()
            }
            thread?.start()
        }
    }

    override fun onDetach(detachEvent: DetachEvent) {
        super.onDetach(detachEvent)

        thread?.interrupt()
    }
}