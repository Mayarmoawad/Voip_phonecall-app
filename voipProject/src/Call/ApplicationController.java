package Call;

import Audio.AudioFileThread;
import Audio.AudioSinusoidalThread;
import Audio.AudioThread;
import Audio.OutputAudio;
import VoIP.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import webphone.webphone;

import java.net.URL;
import java.util.ResourceBundle;

public class ApplicationController implements Initializable {
	
	

    /**
     * MAIN TAB ATTRIBUTES
     */
    @FXML
    private TabPane tabPane;

    @FXML
    private AnchorPane anchorMain;

    @FXML
    private Tab mainTab;

    @FXML
    private ImageView logo;

    @FXML
    private Label connectionLabel;

    @FXML
    private Label receiverLabel;

    @FXML
    private Button callButton;

    @FXML
    private Button hangUpButton;

    /**
     * AUDIO TAB
     */
    @FXML
    private Tab audioTab;

    private Thread callThread;

    @FXML
    private AnchorPane anchorAudio;

    @FXML
    private ImageView audio;

    @FXML
    private Label audioControlLabel;

    @FXML
    private Button spoiledAudioButton;

    @FXML
    private Button fileAudioButton;

    @FXML
    private Button sinusoidalAudioButton;

    @FXML
    private Button stopButton;

    private Thread currentThread;

    /**
     * LOGS TAB
     */
    @FXML
    private Tab logsTab;

    @FXML
    private AnchorPane anchorLogs;

    @FXML
    private TextFlow logsTextFlow;

    @FXML
    private ScrollPane logsScrollPane;

    @FXML
    private ImageView logsButton;

    @FXML
    private Button saveLogsButton;

    @FXML
    private Button updateButton;
    SIPNotifications sipnotifications = null;
    /**
     * SETTINGS TAB
     */
    @FXML
    private Tab settingsTab;

    @FXML
    private AnchorPane anchorSettings;

    @FXML
    private TextField userNameTextBox;

    @FXML
    private Label call_idLabel;

    @FXML
    private Label receiverTagLabel;

    @FXML
    private TextField frequencyTextBox;

    @FXML
    private TextField amplitudeTextBox;

    @FXML
    private Button saveSettingsButton;
    
    

    /**
     * Define the three threads sending audio (RTP) packets.
     */
    AudioThread spoiledThread = new AudioThread();
    AudioFileThread imperialThread = new AudioFileThread();
    AudioSinusoidalThread sinusoidalThread = new AudioSinusoidalThread();
    webphone phone = new webphone();
    //wobj.API_ServerInit("1049");


    /**
     * Initialize the page
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){
    	phone.API_Start();
    	phone.API_ServerInit("1049");
        StringBuilder callID = new StringBuilder();
        for(int index = 0; index < 12; index++){
            callID.append(Request.getCallId().charAt(index));
        }
        this.call_idLabel.setText(callID.toString());
        this.receiverTagLabel.setText(Request.getReceiverTag());
        logsScrollPane.setContent(logsTextFlow);
        
        sipnotifications = new SIPNotifications(phone);
        //start receiving the SIP notifications

        sipnotifications.Start();
        //note: not recommended but it is also possible to receive the notifications via UDP packets instead of API_GetNotifications polling. For that just use the depreacted SIPNotificationsUDP class instead of SIPNotifications class

        //Thread.sleep(100); //you might wait a bit for the JVoIP to construct itself

        //set parameters
        phone.API_SetParameter("loglevel", "5"); //for development you should set the loglevel to 5. for production you should set the loglevel to 1
        phone.API_SetParameter("logtoconsole", "false"); //if the loglevel is set to 5 then a log window will appear automatically. it can be disabled with this setting
        phone.API_SetParameter("polling", "3"); //we will use the API_GetNotifications from our notifications thread, so we can safely disable socket/webphonetojs with this setting
        phone.API_SetParameter("startsipstack", "1"); //auto start the sipstack
        phone.API_SetParameter("register", "0"); //auto register (set to 0 if you don't need to register or if you wish to call the API_Register explicitely later or set to 2 if must register)
        //webphoneobj.API_SetParameter("proxyaddress", "xxx");  //set this if you have a (outbound) proxy
        //webphoneobj.API_SetParameter("transport", "0");  //the default transport is UDP. Set to 1 if you need TCP or to 2 if you need TLS
        //webphoneobj.API_SetParameter("realm", "xxx");  //your sip realm. it have to be set only if it is different from the serveraddress
     phone.API_SetParameter("serveraddress", "172.20.10.2:1049"); //your sip server domain or IP:port (the port number must be set only if not the standard 5060)
       // webphoneobj.API_SetParameter("username", "jvoiptest");
        //
       // webphoneobj.API_SetParameter("password", "jvoiptestpwd");
        //you might set any other required parameters here for your use-case, for example proxyaddres, transport, others. See the parameter list in the documentation.

        //start the SIP stack
        System.out.println("start...");
        //webphoneobj.API_StartGUI(); //you might uncomment this line if you wish to use the built-in GUI
        phone.API_Start();
        try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //you might wait a bit for the sip stack to fully initialize (to make this more correct and reduce the wait time, you might remove this sleep in your app and continue instead when you receive the "START,sip" noification)

        /*
        //register to sip server (optional)
        //however if username/password is set, then it will register automatically at API_Start so this is commented out (you can disable this behavior if you wish by setting the register parameter to 0)
        System.out.println("registering...");
        webphoneobj.API_Register();
        */

    }

    /**
     * Set the Connection status in the relative label                          // MAIN TAB
     *
     * @param value the status, in a string
     */
    public synchronized void setConnectionLabel(String value){
        Platform.runLater(()->{this.connectionLabel.setText(value);});
    }

    /**
     * Call the mjUA "Bob", read the response to the Invite Request and send
     *  ACK if the mJUA sends a 200OK Response
     *
     * @param event press on Call button
     */
    @FXML
    void call(ActionEvent event) { 
    	
    	phone.API_Call( -1, "BOB@172.20.10.2:1049");
    	// Start the call if the Session is not active yet
    	
    	//new JVoIPTest();
		/* x.webphoneobj.API_Call("1234@172.20.10.2:1049"); */
		
//		  if(!Session.isActive() && (Response.getServerAnswer() == null ||
//		  Response.getServerAnswer().equals("200"))) { callThread = null;
//		  Session.clear(); callThread = new Thread(new UserAgent());
//		  callThread.start(); }
//		  
		  
		  
		
    }

    /**
     * Hang Up the call sending a Bye Request is the Session is active and read
     *  the ACK Response sent by the mjUA "Bob"
     *
     * @param event press on Hang Up button
     */
    @FXML
    void hangUp(ActionEvent event) {
    	
    	phone.API_Hangup( -1);
    	
    	 phone.API_Stop(); //stop the sip stack (this will also unregister)
         sipnotifications.Stop();
//        if (Session.isActive()) {
//            Response.listen4CloseThread.interrupt();
//            UserAgent.closeCall();
//        }else {
//        	System.out.println("Connection terminated");
//        	Program.controller.setConnectionLabel("BYE");
//        }
    }

    /**
     * Receive mjUA "Bob" RTP Packets, edit the packet's payload with randomly      // AUDIO
     *  generated bytes and send the packet back to him
     *
     * @param event press on Spoiled Audio button
     */
    @FXML
    void sendSpoiledAudio(ActionEvent event) {
        if (!OutputAudio.isRunning()) {
            new Thread(spoiledThread).start();
        }
    }

    /**
     * Send the mjUA "Bob" the Imperial_March.wav in resources/audio folder
     *
     * @param event press on Spoiled Audio button
     */
    @FXML
    void sendImperialMarch(ActionEvent event) {
        if (!OutputAudio.isRunning()) {
            new Thread(imperialThread).start();
        }
    }

    /**
     * Start sending a sine wave in RTP Packets to the UserAgent "Bob"
     *
     * @param event press on Sinusoidal Audio button
     */
    @FXML
    void sendSinusoidalAudio(ActionEvent event) {
        if (!OutputAudio.isRunning()) {
            new Thread(sinusoidalThread).start();
        }
    }

    /**
     * Stop sending audio
     *
     * @param event press on Stop button
     */
    @FXML
    synchronized void stopAudio(ActionEvent event) {

        OutputAudio.setRunning(false);
    }

    /**
     * Save logs on a external file in /requests/logs.txt              //  LOGS TAB
     *
     * @param event press on Save button
     */
   // @FXML
    /*void saveLogs(ActionEvent event) {
        Session.save();
    }*/

    /**
     * Update the Text Flow in the Scroll Pane with the updated logs
     *
     * @param event press on Update button
     */
    /*@FXML
    void updateLogs(ActionEvent event) {
        Text text = new Text(Session.logsMessage());
        this.logsTextFlow.getChildren().clear();
        this.logsTextFlow.getChildren().add(text);
        logsScrollPane.setContent(logsTextFlow);
    }*/

    /**
     * Save the new settings of the UserAgent                                   // SETTINGS TAB
     *
     * @param event press on Save button
     */
    @FXML
    void saveSettings(ActionEvent event) {
        String newName = userNameTextBox.getText();                                 // Take the string in the TextBox
        String newFrequency = frequencyTextBox.getText();
        String newAmplitude = amplitudeTextBox.getText();

        if(!Session.isActive() && !newName.equals(""))
            Request.setSenderName(newName);

        if(tryParseDouble(newFrequency))                                            // Set the new Frequency
            AudioSinusoidalThread.setFrequency(Double.parseDouble(newFrequency));

        if(tryParseDouble(newAmplitude))
            AudioSinusoidalThread.setAmplitude(Double.parseDouble(newAmplitude));   // Set the new Amplitude
    }

    /**
     * Set the Receiver Tag in the relative label
     *
     */
    public synchronized void setReceiverTagLabel(){
        StringBuilder receiverTag = new StringBuilder();
        for(int index = 4; index < Request.getReceiverTag().length(); index++){
            receiverTag.append(Request.getReceiverTag().charAt(index));
        }
        Platform.runLater(()->{this.receiverTagLabel.setText(receiverTag.toString());});
    }

    /**
     * Try Parse Double method
     *
     * @param value the value to convert to double
     * @return true if the string can be converted to double,
     *  false otherwise
     */
    public boolean tryParseDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
