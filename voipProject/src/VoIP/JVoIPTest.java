package VoIP;

import webphone.*; //import JVoIP. You will have an error here if you haven't added the JVoIP.jar to your project


public class JVoIPTest {

    public webphone webphoneobj = null;
    SIPNotifications sipnotifications = null;

    /**
    * Construct and show the application.
    */
    public JVoIPTest() {
        Go();
    }

    /**
    * Application entry point.
    */
    public static void main(String[] args) {
        try {
            new JVoIPTest();
        }catch(Exception e) {System.out.println("Exception at main: "+e.getMessage()+"\r\n"+e.getStackTrace()); }

    }

    /**
    * This is the important function where all the real work is done.
    * In this example we start the JVoIP (a webphone instance), set parameters and make an outbound call.
    */

    void Go()
    {
        try{
            System.out.println("init...");

            //create a JVoIP instance
            webphoneobj = new webphone();

            //create the SIPNotifications object to catch the events from JVoIP
            sipnotifications = new SIPNotifications(webphoneobj);
            //start receiving the SIP notifications

            sipnotifications.Start();
            //note: not recommended but it is also possible to receive the notifications via UDP packets instead of API_GetNotifications polling. For that just use the depreacted SIPNotificationsUDP class instead of SIPNotifications class

            //Thread.sleep(100); //you might wait a bit for the JVoIP to construct itself

            //set parameters
            webphoneobj.API_SetParameter("loglevel", "5"); //for development you should set the loglevel to 5. for production you should set the loglevel to 1
            webphoneobj.API_SetParameter("logtoconsole", "false"); //if the loglevel is set to 5 then a log window will appear automatically. it can be disabled with this setting
            webphoneobj.API_SetParameter("polling", "3"); //we will use the API_GetNotifications from our notifications thread, so we can safely disable socket/webphonetojs with this setting
            webphoneobj.API_SetParameter("startsipstack", "1"); //auto start the sipstack
            webphoneobj.API_SetParameter("register", "0"); //auto register (set to 0 if you don't need to register or if you wish to call the API_Register explicitely later or set to 2 if must register)
            //webphoneobj.API_SetParameter("proxyaddress", "xxx");  //set this if you have a (outbound) proxy
            //webphoneobj.API_SetParameter("transport", "0");  //the default transport is UDP. Set to 1 if you need TCP or to 2 if you need TLS
            //webphoneobj.API_SetParameter("realm", "xxx");  //your sip realm. it have to be set only if it is different from the serveraddress
         webphoneobj.API_SetParameter("serveraddress", "172.20.10.2:1049"); //your sip server domain or IP:port (the port number must be set only if not the standard 5060)
           // webphoneobj.API_SetParameter("username", "jvoiptest");
            //
           // webphoneobj.API_SetParameter("password", "jvoiptestpwd");
            //you might set any other required parameters here for your use-case, for example proxyaddres, transport, others. See the parameter list in the documentation.

            //start the SIP stack
            System.out.println("start...");
            //webphoneobj.API_StartGUI(); //you might uncomment this line if you wish to use the built-in GUI
            webphoneobj.API_Start();
            Thread.sleep(200); //you might wait a bit for the sip stack to fully initialize (to make this more correct and reduce the wait time, you might remove this sleep in your app and continue instead when you receive the "START,sip" noification)

            /*
            //register to sip server (optional)
            //however if username/password is set, then it will register automatically at API_Start so this is commented out (you can disable this behavior if you wish by setting the register parameter to 0)
            System.out.println("registering...");
            webphoneobj.API_Register();
            */

            System.out.println("SIP stack initialized. Press enter to make a call");
            WaitForEnterKeyPress();

            //make an outbound call
            System.out.println("calling...");
            webphoneobj.API_Call( -1, "1111@172.20.10.2:1049");

            //normally your app logic might be continued elsewhere (handling user interactions such as disconnect button click) and you should process the notifications about the call state changes in the SIPNotification.java -> ProcessNotifications() function

            //wait for key press
            System.out.println("Call initiated. Press enter to stop");
            WaitForEnterKeyPress();

            //disconnect the call and stop the SIP stack
            System.out.println("closing...");
            webphoneobj.API_Hangup( -1);  //disconnect the call

            webphoneobj.API_Stop(); //stop the sip stack (this will also unregister)
            sipnotifications.Stop(); //stsop the JVoIP notification listener
            System.out.println("Finished. Press enter to exit");

            WaitForEnterKeyPress();
            System.exit(0); //exit the JVM

        }catch(Exception e) {System.out.println("Exception at Go: "+e.getMessage()+"\r\n"+e.getStackTrace()); }

    }

    void WaitForEnterKeyPress()
    {
    	
        try{
            //skip existing (old) input
            int avail = System.in.available();
            if(avail > 0) System.in.read(new byte[avail]);
        }catch(Exception e) {}

        try{
            //wait for enter press
            while (true)
            {
                int ch = System.in.read();
                if(ch == '\n') break;
            }
        }catch(Exception e) {}
    }
}

