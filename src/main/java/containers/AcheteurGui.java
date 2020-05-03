package containers;

import agents.AcheteurAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class AcheteurGui extends Application {
    private VBox vBox;
    private ListView stringListView;
    private BorderPane root;
    private AcheteurAgent acheteurAgent;

    public static void main(String[] args) {
        launch(args);
    }

    public AcheteurAgent getAcheteurAgent() {
        return acheteurAgent;
    }

    public void setAcheteurAgent(AcheteurAgent acheteurAgent) {
        this.acheteurAgent = acheteurAgent;
    }

    private void initialize() {
        stringListView = new ListView<String>();
        vBox = new VBox();
        vBox.getChildren().addAll(stringListView);
    }

    private void addToRoot() {
        root.setCenter(stringListView);
    }

    private void setEvents() {
    }

    private void startContainer() throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        profile.setParameter(ProfileImpl.CONTAINER_NAME, "Acheteurs");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("Acheteur1", "agents.AcheteurAgent", new Object[]{this});
        agentController.start();
    }

    public void logMessage(ACLMessage aclMessage) {
        Platform.runLater(() -> {
            stringListView.getItems().add(aclMessage.getPerformative() + "::" + aclMessage.getSender().getLocalName() + "::" + aclMessage.getContent());
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("AcheteurGUI");
        startContainer();
        root = new BorderPane();
        initialize();
        addToRoot();
        setEvents();
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
