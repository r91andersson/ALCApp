package integrum.bioniclimbcontroller.Bluetooth;

import android.support.v4.content.res.TypedArrayUtils;

import java.util.Arrays;
import java.util.stream.IntStream;

import integrum.bioniclimbcontroller.Constants;

/**
 * Created by Robin on 2016-10-21.
 */

public class CommunicationProtocol extends MessageStruct {

    private static char[] data;
    private static Integer[] COMMANDS =Constants.COMMANDS_STRING;

    public MessageStruct parseMessage(byte[] message) {
        boolean parse = true;
        MessageStruct msg = new MessageStruct();
        int checksum_ver = 0;

        int header = (0xFF) & message[0];
        int cmd = (0xFF) & message[1];
        int nDataBytes = (0xFF) & message[2];
        int[] data = new int[nDataBytes];
        int checksum = (0xFF) & message[3 + nDataBytes];

            //Check header format
            if (header != Constants.HEADER) {
                msg.setSTATUS(Constants.WRONG_CMD_STRUCTURE);
                return msg;
            }

            //Verify command is in proper format and exists
            if (!Arrays.asList(COMMANDS).contains(cmd)) {
                msg.setSTATUS(Constants.WRONG_CMD);
                return msg;
            }

            // Check data length
           // if (nDataBytes > Constants.MAX_DATA_LENGTH) {
           //     msg.setSTATUS(Constants.WRONG_CMD_STRUCTURE);
           //     return msg;
           // }

            // Calculate checksum
            for (int i = 0; i < nDataBytes; i++) {
                data[i] = (0xFF) & message[i + 3];
                checksum_ver = checksum_ver + data[i];
            }

            // Convert to byte format
            checksum_ver = (checksum_ver + header + cmd + nDataBytes) % 256;

            // Verify checksum
            if (checksum != checksum_ver) {
                msg.setSTATUS(Constants.WRONG_CHECKSUM);
                return msg;
            }

            // Everything went fine, return the full message struct
            return createMessage(cmd, data, checksum_ver, Constants.MESSAGE_OK);

        }



    public MessageStruct createMessage(int cmd,int[] data, Integer checksum,int status){
        MessageStruct msg= new MessageStruct();
        msg.setHEADER(Constants.HEADER);
        msg.setCMD(cmd);
        msg.setDATA(data);
        msg.setN_BYTES(data.length);
        msg.setSTATUS(status);
        if(checksum != null) {
            msg.setCS((int)checksum);
        } else{
            checksum = generateCheckSum(msg);
            msg.setCS(checksum);
        }
        return msg;
    }

    public MessageStruct createMessage(int cmd,int[] data){
        MessageStruct msg= new MessageStruct();
        msg.setHEADER(Constants.HEADER);
        msg.setCMD(cmd);
        msg.setDATA(data);
        msg.setN_BYTES(data.length);
      // msg.setSTATUS(status);
        int checksum = generateCheckSum(msg);
        msg.setCS(checksum);
        return msg;
    }

    public int generateCheckSum(MessageStruct msg){
        int checksum;
        int[] data=msg.getDATA();
        int nDataBytes=msg.getN_BYTES();

        checksum =  msg.getHEADER()+ msg.getCMD() + msg.getN_BYTES();

        for (int i=0;i<nDataBytes;i++){
            checksum=checksum+data[i];
        }

        return checksum;
    }

    public boolean checkSum (String message, char checksum){
        boolean check = false;


        return check;
    }

    public static char[] getData() {
        return data;
    }

    public static void setData(char[] data) {
        CommunicationProtocol.data = data;
    }
}
