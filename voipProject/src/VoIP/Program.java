package VoIP;

import Call.ApplicationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import webphone.webphone;

import java.io.IOException;

/**
 * VoIP.Main Class
 * Run the UI and open the Application page.
 *
 */
public class Program extends Application {
    public static ApplicationController controller;

    private static String UI = "../Call/Application.fxml";

    public void start(Stage primaryStage) throws IOException {
    	//new JVoIPTest();
    	
    	//webphone wobj = new webphone();
		//wobj.API_SetParameter("0", "192.168.0.127:");
		//wobj.API_Start();
		//wobj.API_Call("192.168.0.127:5678");
		//wobj.API_ServerInit("1049");
		//wobj.API_SetParameter("signalingport","1049");
    	
        FXMLLoader loader = new FXMLLoader(getClass().getResource(UI));
        Parent root = loader.load();
        controller = loader.getController();
        Scene frame = new Scene(root);
        primaryStage.getIcons().add(new Image("/images/voip.png"));
        primaryStage.setResizable(false);
        primaryStage.setTitle("VoIP");
        primaryStage.setScene(frame);
        
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        //new JVoIPTest();
    }

    public static void main(String[] args) {
    	 
        launch(args);
      
    }
}