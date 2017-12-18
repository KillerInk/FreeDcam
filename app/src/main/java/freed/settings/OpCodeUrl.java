package freed.settings;

/**
 * Created by KillerInk on 18.12.2017.
 */

public class OpCodeUrl {
    private int ID;
    private String opcode2Url;
    private String opcode3Url;

    public OpCodeUrl(int id, String opcode2Url, String opcode3Url)
    {
        this.ID = id;
        this.opcode2Url = opcode2Url;
        this.opcode3Url = opcode3Url;
    }

    public int getID() {
        return ID;
    }

    public String getOpcode2Url() {
        return opcode2Url;
    }

    public String getOpcode3Url() {
        return opcode3Url;
    }

/*     <camera id="0">  0 = back, 1 = front, 2 = nextcamera etc
            <opcode2></opcode2>
            <opcode3></opcode3>
        <camera>*/
    public String getXml()
    {
        String t = "<camera id=" +String.valueOf("\"") +String.valueOf(ID) +String.valueOf("\"")  +">" + "\r\n";
        t += "<opcode2>"+opcode2Url+"</opcode2>"+ "\r\n";
        t += "<opcode3>"+opcode3Url+"</opcode3>"+ "\r\n";
        t += "</camera>\r\n";
        return t;
    }
}
