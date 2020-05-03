package containers;

import agents.ConsumerAgent;
import jade.core.Agent;
import jade.core.Location;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerGui extends Application {
    private BorderPane root;
    private HBox hBox;
    private Label label;
    private TextField textFieldLivre;
    private Button buttonLivre;
    private VBox vBox;
    private ListView<String> stringListView;
    private ConsumerAgent consumerAgent;

    public static void main(String[] args) throws ControllerException {
        launch(args);
    }

    public ConsumerAgent getConsumerAgent() {
        return consumerAgent;
    }

    public void setConsumerAgent(ConsumerAgent consumerAgent) {
        this.consumerAgent = consumerAgent;
    }

    public void startContainer() throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.CONTAINER_NAME, "Consumers");
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer container = runtime.createAgentContainer(profile);
        container.start();
        AgentController agentController = container.createNewAgent("Consumer1", "agents.ConsumerAgent", new Object[]{this});
        agentController.start();

    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()-> {
            stringListView.getItems().add(aclMessage.getPerformative()+"::"+aclMessage.getSender().getLocalName()+"::"+aclMessage.getContent());
        });
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ConsommateurGUI");
        root = new BorderPane();
        startContainer();
        init();
        addToRoot();
        setEvents();
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setEvents() {
        buttonLivre.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                String livre = textFieldLivre.getText().trim();
                if (livre.length() == 0) return;
                stringListView.getItems().add(livre);
                GuiEvent guiEvent = new GuiEvent(this, 1);
                guiEvent.addParameter(livre);
                consumerAgent.onGuiEvent(guiEvent);

            }
        });
    }

    public void init() {
        hBox = new HBox();
        label = new Label("Livre : ");
        textFieldLivre = new TextField();
        buttonLivre = new Button("valider");
        stringListView = new ListView<>();
        vBox = new VBox();
        hBox.setPadding(new Insets(10));
        vBox.setPadding(new Insets(10));
        hBox.setSpacing(5);
        vBox.getChildren().addAll(stringListView);
        hBox.getChildren().addAll(label, textFieldLivre, buttonLivre);
    }

    public void addToRoot() {
        root.setTop(hBox);
        root.setCenter(vBox);
    }
}
