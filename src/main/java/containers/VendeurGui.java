package containers;

import agents.VendeurAgent;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;


public class VendeurGui extends Application {
    private BorderPane root;
    private VBox vBox;
    private HBox hBox;
    private TextField vendeurTextField;
    private Label vendeurIDLabel;
    private Button deployButton;
    private ListView<String> stringListView;
    private AgentContainer agentContainer;
    private VendeurAgent vendeurAgent;
    private ArrayList<VendeurAgent> vendeurAgents = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);

    }

    public void init() {
        vBox = new VBox();
        deployButton = new Button("deploy");
        hBox = new HBox();
        vendeurIDLabel = new Label("LocalID: ");
        vendeurTextField = new TextField();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(5);
        stringListView = new ListView<>();
        vBox.getChildren().addAll(stringListView);
        hBox.getChildren().addAll(vendeurIDLabel, vendeurTextField, deployButton);
    }

    private void addToRoot() {
        root.setCenter(vBox);
        root.setTop(hBox);
    }

    private void setEvents() {


        deployButton.setOnMouseClicked(event -> {
            String vendeurLocalID = vendeurTextField.getText().trim();
            if (vendeurLocalID.length() == 0) return;
            try {
                AgentController agentController = agentContainer.createNewAgent(vendeurLocalID, "agents.VendeurAgent", new Object[]{this});
                agentController.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();
        primaryStage.setTitle("Vendeur-Gui");
        root = new BorderPane();
        init();
        addToRoot();
        setEvents();
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void startContainer() throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        profile.setParameter(ProfileImpl.CONTAINER_NAME, "Vendeurs");
        agentContainer = runtime.createAgentContainer(profile);
        agentContainer.start();

    }

    public void logMessage(Agent agent, ACLMessage aclMessage) {
        Platform.runLater(() -> {
            stringListView.getItems().add(agent.getLocalName() + "->" + aclMessage.getPerformative() + "::" + aclMessage.getSender().getLocalName() + "::" + aclMessage.getContent());
        });
    }

    public void setVendeurAgent(VendeurAgent vendeurAgent) {

        this.vendeurAgent = vendeurAgent;
        vendeurAgents.add(vendeurAgent);
    }

    public VendeurAgent getVendeurAgent() {
        return vendeurAgent;
    }
}
