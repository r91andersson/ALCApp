package integrum.bioniclimbcontroller.Bluetooth;

/**
 * Created by Robin on 2016-10-21.
 */
public class MessageStruct {
    private int HEADER;
    private int CMD;
    private int N_BYTES;
    private int[] DATA;
    private int CS;
    private int STATUS;

    public MessageStruct(){
        this.HEADER=0;
        this.CMD=0;
        this.N_BYTES=0;
        this.DATA=null;
        this.CS=0;
        this.STATUS=0;
    }

    public MessageStruct(int header, int cmd, int nBytes, int[] data, int cs,int status){
        super();
        this.HEADER=header;
        this.CMD=cmd;
        this.N_BYTES=nBytes;
        this.DATA=data;
        this.CS=cs;
        this.STATUS=status;
    }

    public int getHEADER() {
        return HEADER;
    }

    public void setHEADER(int HEADER) {
        this.HEADER = HEADER;
    }

    public int getCMD() {
        return CMD;
    }

    public void setCMD(int CMD) {
        this.CMD = CMD;
    }

    public int getN_BYTES() {
        return N_BYTES;
    }

    public void setN_BYTES(int n_BYTES) {
        N_BYTES = n_BYTES;
    }

    public int[] getDATA() {
        return DATA;
    }

    public void setDATA(int[] DATA) {
        this.DATA = DATA;
    }

    public int getCS() {
        return CS;
    }

    public void setCS(int CS) {
        this.CS = CS;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public String getMessageAsString(){
        int msgLength=getN_BYTES();

        char header = (char) getHEADER();
        char cmd = (char) getCMD();
        char datalength =(char) msgLength;
        char [] dataChar= new char[msgLength];
        char cs= (char) getCS();
        int [] dataInt = getDATA();

        for (int i=0; i<msgLength;i++){
            dataChar[i] = (char) dataInt[i];
        }

        char []a=new char[4+msgLength];
        a[0]=header;
        a[1]=cmd;
        a[2]=datalength;

        for(int j=3;j<3+msgLength;j++){
            a[j]= (char) dataChar[j-3];
        }

        a[3+msgLength]=cs;

        String message= String.valueOf(a);

        return message;
    }
}
