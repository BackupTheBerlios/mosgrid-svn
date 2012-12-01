- Theres an article on the useage of the ChemDoodle widget in the developer-wiki.

- Don't bother on the error in the WebContent directory! This seems to be a bug in the ChemDoodle JavaScript library, but works anyhow.

- Keep the chemdoodle.jar in the project synchronized with the file on the webserver

- Use the MANIFEST.MF in WebContent/META-INF for building the jar

- You can compile the widgetset locally if you change to DefaultWidgetSet in gwt.xml

- You only have to recompile the widgetset after changes in classes in the widget package