
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class AppAyuda extends Application {

    private Scene scene;

    @Override
    public void start(Stage stage) {
        // create scene
        stage.setTitle("APP AYUDA");
        scene = new Scene(new Browser(), 750, 500, Color.web("#666970"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Browser extends Region {

    private HBox toolBar;
    private static String[] captions = new String[]{
        "Moodle",
        "Facebook",
        "Twitter",
        "Menu Html"
    };
    private static String[] urls = new String[]{
        "http://www.ieslosmontecillos.es/moodle",
        "http://www.facebook.com",
        "http://www.twitter.com",
        AppAyuda.class.getResource("menu.html").toExternalForm()
    };
    final Hyperlink[] hpls = new Hyperlink[captions.length];
    WebView smallView = new WebView();
    final ComboBox comboBox = new ComboBox();

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    public Browser() {
        //apply the styles
        getStyleClass().add("browser");
        //Para tratar lo tres enlaces
        for (int i = 0; i < captions.length; i++) {
            Hyperlink hpl = hpls[i] = new Hyperlink(captions[i]);
            final String url = urls[i];

            //proccess event
            hpl.setOnAction((ActionEvent e) -> {
                webEngine.load(url);
            });
        }
        // load the web page
        webEngine.load("http://www.ieslosmontecillos.es");
        // create the toolbar
        toolBar = new HBox();
        toolBar.setAlignment(Pos.CENTER);
        toolBar.getStyleClass().add("browser-toolbar");
        comboBox.setPrefWidth(60);
        toolBar.getChildren().add(comboBox);
        toolBar.getChildren().addAll(hpls);
        toolBar.getChildren().add(createSpacer());

        final WebHistory history = webEngine.getHistory();

            history.getEntries().addListener((ListChangeListener.Change<? extends WebHistory.Entry> c) -> {
                c.next();
                for (WebHistory.Entry e : c.getRemoved())
                {
                    comboBox.getItems().remove(e.getUrl());
                }
                for (WebHistory.Entry e : c.getAddedSubList())
                {
                    comboBox.getItems().add(e.getUrl());
                }
        });
            //Se define el comportamiento del combobox
            comboBox.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent ev)
                {
                    int offset = comboBox.getSelectionModel().getSelectedIndex() - history.getCurrentIndex();
                    history.go(offset);
                }
            });

            smallView.setPrefSize(120, 80);
            //handle popup windows
            webEngine.setCreatePopupHandler((PopupFeatures config) -> {
                smallView.setFontScale(0.8);
                if (!toolBar.getChildren().contains(smallView))
                {
                    toolBar.getChildren().add(smallView);
                }
                return smallView.getEngine();
        });

        //add components
        getChildren().add(toolBar);
        getChildren().add(browser);
    }

    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        double tbHeight = toolBar.prefHeight(w);
        layoutInArea(browser, 0, 0, w, h - tbHeight, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(toolBar, 0, h
                - tbHeight, w, tbHeight, 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computePrefWidth(double height) {
        return 750;

    }

    @Override
    protected double computePrefHeight(double width) {
        return 500;
    }
}
