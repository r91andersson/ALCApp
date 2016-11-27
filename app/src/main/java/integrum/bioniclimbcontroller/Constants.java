package integrum.bioniclimbcontroller;

/**
 * Created by Robin on 2016-10-04.
 */
public interface Constants {

    // MessageStruct types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";





    // Bluetooth service states
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device



    public static int HEADER = 0x7F;
    public static int CMD = 0xE0;
    public static int N_BYTES = 0x00;
    public static int DATA = 0x00;
    public static int CS = 0x00;


    public static int MAX_DATA_LENGTH=           50;
    public static int CMD_HEADER=			  	 0x7F;
    public static int ANSWER_HEADER=		  	 0x7F;
    public static int NEW_SAMPLE=			  	 0xAA;

    // Commands & Error's messages
    public static int NEW_FEATURES=		  	     0xAB;
    public static int STATUS_CHECK=              0x21;
    public static int UNLOCK_NEUROMOTUS=		 0x01;
    public static int FIRMWARE_VERSION_READ=	 0x02;
    public static int BATTERY_VOLTAGE_READ=	     0x03;
    public static int CONFIG_REG_READ=	  		 0x10;
    public static int GAIN_SET=		  		     0x11;
    public static int SF_SET=					 0x12;
    public static int DATA_RATE_SET=			 0x13;
    public static int FILTERS_ENABLE_SET=		 0x14;
    public static int TEST_SIGNAL_ENABLE_SET=	 0x15;
    public static int FEATURES_ENABLES_SET=	     0x16;
    public static int START_RAW_ACQ=			 0x17;
    public static int START_FEATURES_ACQ=		 0x18;
    public static int STOP_ACQ=				     0x19;
    public static int UPDATE_MOVEMENT=			 0x23;
    public static int SET_COMMAND_MODE=		     0x31;
    public static int SET_CONTROL_MODE=          0x30;
    public static int NS_ENABLE_DISABLE=		 0xB0;
    public static int STREAMING_IMU_DATA=        0x40;

    public static Integer[] COMMANDS_STRING = new Integer[]{NEW_FEATURES,STATUS_CHECK,UNLOCK_NEUROMOTUS,
            FIRMWARE_VERSION_READ,BATTERY_VOLTAGE_READ,CONFIG_REG_READ,GAIN_SET,SF_SET,DATA_RATE_SET,
            FILTERS_ENABLE_SET,FEATURES_ENABLES_SET,START_RAW_ACQ,START_FEATURES_ACQ,STOP_ACQ,UPDATE_MOVEMENT,
            SET_COMMAND_MODE,SET_CONTROL_MODE,NS_ENABLE_DISABLE,STREAMING_IMU_DATA};

    public static int COMMAND_ACK=		 		 0xE0;
    public static int NEUROMOTUS_LOCKED=		 0x80;
    public static int WRONG_CMD_STRUCTURE=		 0x81;
    public static int WRONG_CMD=				 0x82;
    public static int WRONG_CHECKSUM=			 0x83;
    public static int BATTERY_ERROR=			 0x84;
    public static int WT12_MESSAGE=			     0x85;
    public static int MESSAGE_OK=                0x86;


    public static int DEBUG_PASSWORD=           5555;
    public static int DEBUG_PW_REQUEST=          1;
    public static final int DEBUG_MODE=          1;
    public static final int USER_MODE=           0;

    // Used in Calibration Instructions class (onActivity result method)
    public static int RESULT_REDO=               2;
    public static int RESULT_MENU=               1;
    public static int RESULT_OK=                 1;


    // Used in control
    public static int DIRECT_CONTROL=           1;
    public static int PATTERN_RECOGNITION=      2;
    public static int PATTERN_RECOGNITION_LIST= 3;


    // Used in main activity
    public static int SEND_MESSAGE=             3;


    // ------------------- LOG IN Constants -----------------------//
    public static final String BASE_URL = "http://login.bctechnologies.se/";
    public static final String REGISTER_OPERATION = "register";
    public static final String LOGIN_OPERATION = "login";
    public static final String CHANGE_PASSWORD_OPERATION = "chgPass";

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String IS_LOGGED_IN = "isLoggedIn";

    public static final String NAME = "user_name";
    public static final String EMAIL = "email";
    public static final String UNIQUE_ID = "unique_id";


    public static final String [] movementNames = new String[] {"Open Hand",
            "Close Hand",
            "Switch",
            "Pronation",
            "Supination",
            "Cocontraction",
            "Flex Hand",
            "Extend hand",
            "Side Grip",
            "Fine Grip",
            "Agree",
            "Pointer",
            "Thumb Extend",
            "Thumb Flex",
            "Tumb Abduc 1",
            "Thumb Abduc 2",
            "Flex Elbow",
            "Extend Elbow",
            "Index Flex",
            "Index Extend",
            "Middle Flex",
            "Middle Extend",
            "Ring Flex",
            "Ring Extend",
            "Little Flex",
            "Little Extend"};


}
