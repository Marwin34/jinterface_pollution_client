import com.ericsson.otp.erlang.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PollutionClient {

    public static void main(String[] args) throws Exception {

        boolean running = true;

        OtpNode myNode = new OtpNode("client");

        OtpMbox myMbox = myNode.createMbox("pollution_client");

        OtpErlangObject myObject;

        OtpErlangTuple myMsg;

        OtpErlangPid pid = myMbox.self();

        //wait for id from erlang
        myObject = myMbox.receive();

        myMsg = (OtpErlangTuple) myObject;

        OtpErlangPid target = (OtpErlangPid) myMsg.elementAt(0);

        System.out.println(target);

        OtpErlangAtom requestAtom = new OtpErlangAtom("request");

        while (running) {
            OtpErlangTuple request = null;

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            boolean commandParsed = true;

            switch (input) {
                case "addStation":
                    request = parseAddStation(reader);
                    break;
                case "addValue":
                    request = parseAddValue(reader);
                    break;
                case "removeValue":
                    request = parseRemoveValue(reader);
                    break;
                case "getOneValue":
                    request = parseGetOneValue(reader);
                    break;
                case "getStationMean":
                    request = parseGetStationMean(reader);
                    break;

                case "quit":
                    request = parseQuit();
                    running = false;
                    break;

                default:
                    commandParsed = false;
                    break;
            }

            if (commandParsed) {
                OtpErlangObject[] requestObject = new OtpErlangObject[3];
                requestObject[0] = requestAtom;
                requestObject[1] = pid;
                requestObject[2] = request;

                OtpErlangTuple message = new OtpErlangTuple(requestObject);

                myMbox.send(target, message);

                myObject = myMbox.receive();

                myMsg = (OtpErlangTuple) myObject;

                System.out.println(myMsg.elementAt(1));
            } else {
                System.out.println("Unknown command!");
            }

        }
    }

    private static OtpErlangTuple parseAddStation(BufferedReader reader) throws Exception {
        System.out.println("Name:");
        String name = reader.readLine();

        System.out.println("Latitude:");
        String latitude = reader.readLine();

        System.out.println("Longitude:");
        String longitude = reader.readLine();

        OtpErlangObject[] datasObj = new OtpErlangObject[2];
        datasObj[0] = new OtpErlangString(name);
        datasObj[1] = coordsInTuple(latitude, longitude);

        OtpErlangTuple datas = new OtpErlangTuple(datasObj);

        OtpErlangAtom addStation = new OtpErlangAtom("addStation");

        OtpErlangObject[] requestObj = new OtpErlangObject[2];
        requestObj[0] = addStation;
        requestObj[1] = datas;


        return new OtpErlangTuple(requestObj);
    }

    //{request, Pid, {addValue, {Key, Date, Type, Value}}} ->
    private static OtpErlangTuple parseAddValue(BufferedReader reader) throws Exception {
        System.out.println("Name:");
        String name = reader.readLine();

        System.out.println("Date: (YY:MM:DD)");
        String date = reader.readLine();

        System.out.println("Time: (HH:MM:SS)");
        String time = reader.readLine();

        System.out.println("Type");
        String type = reader.readLine();

        System.out.println("Value");
        String value = reader.readLine();

        float valueVal = Float.parseFloat(value);

        OtpErlangObject[] datasObj = new OtpErlangObject[4];
        datasObj[0] = new OtpErlangString(name);
        datasObj[1] = datetimeToTuple(date, time);
        datasObj[2] = new OtpErlangString(type);
        datasObj[3] = new OtpErlangFloat(valueVal);

        OtpErlangTuple datas = new OtpErlangTuple(datasObj);

        OtpErlangAtom addStation = new OtpErlangAtom("addValue");

        OtpErlangObject[] requestObj = new OtpErlangObject[2];
        requestObj[0] = addStation;
        requestObj[1] = datas;


        return new OtpErlangTuple(requestObj);
    }

    //{request, Pid, {removeValue, {Key, Date, Type}}} ->
    private static OtpErlangTuple parseRemoveValue(BufferedReader reader) throws Exception {
        System.out.println("Name:");
        String name = reader.readLine();

        System.out.println("Date: (YY:MM:DD)");
        String date = reader.readLine();

        System.out.println("Time: (HH:MM:SS)");
        String time = reader.readLine();

        System.out.println("Type");
        String type = reader.readLine();

        OtpErlangObject[] datasObj = new OtpErlangObject[3];
        datasObj[0] = new OtpErlangString(name);
        datasObj[1] = datetimeToTuple(date, time);
        datasObj[2] = new OtpErlangString(type);

        OtpErlangTuple datas = new OtpErlangTuple(datasObj);

        OtpErlangAtom addStation = new OtpErlangAtom("removeValue");

        OtpErlangObject[] requestObj = new OtpErlangObject[2];
        requestObj[0] = addStation;
        requestObj[1] = datas;


        return new OtpErlangTuple(requestObj);
    }

    //{request, Pid, {getOneValue, {Key, Date, Type}}} ->
    private static OtpErlangTuple parseGetOneValue(BufferedReader reader) throws Exception {
        System.out.println("Name:");
        String name = reader.readLine();

        System.out.println("Date: (YY:MM:DD)");
        String date = reader.readLine();

        System.out.println("Time: (HH:MM:SS)");
        String time = reader.readLine();

        System.out.println("Type");
        String type = reader.readLine();

        OtpErlangObject[] datasObj = new OtpErlangObject[3];
        datasObj[0] = new OtpErlangString(name);
        datasObj[1] = datetimeToTuple(date, time);
        datasObj[2] = new OtpErlangString(type);

        OtpErlangTuple datas = new OtpErlangTuple(datasObj);

        OtpErlangAtom addStation = new OtpErlangAtom("getOneValue");

        OtpErlangObject[] requestObj = new OtpErlangObject[2];
        requestObj[0] = addStation;
        requestObj[1] = datas;


        return new OtpErlangTuple(requestObj);
    }

    //{request, Pid, {getStationMean, {Key, Type}}} ->
    private static OtpErlangTuple parseGetStationMean(BufferedReader reader) throws Exception {
        System.out.println("Name:");
        String name = reader.readLine();

        System.out.println("Type");
        String type = reader.readLine();

        OtpErlangObject[] datasObj = new OtpErlangObject[2];
        datasObj[0] = new OtpErlangString(name);
        datasObj[1] = new OtpErlangString(type);

        OtpErlangTuple datas = new OtpErlangTuple(datasObj);

        OtpErlangAtom addStation = new OtpErlangAtom("getStationMean");

        OtpErlangObject[] requestObj = new OtpErlangObject[2];
        requestObj[0] = addStation;
        requestObj[1] = datas;


        return new OtpErlangTuple(requestObj);
    }

    private static OtpErlangTuple parseQuit() {
        OtpErlangObject[] requestObj = new OtpErlangObject[1];
        requestObj[0] = new OtpErlangAtom("stop");
        return new OtpErlangTuple(requestObj);
    }

    private static OtpErlangTuple coordsInTuple(String latitude, String longitude) {
        float latitudeVal = Float.parseFloat(latitude);
        float longitudeVal = Float.parseFloat(longitude);

        OtpErlangObject[] coords = new OtpErlangObject[2];
        coords[0] = new OtpErlangFloat(latitudeVal);
        coords[1] = new OtpErlangFloat(longitudeVal);

        return new OtpErlangTuple(coords);
    }

    private static OtpErlangTuple datetimeToTuple(String date, String time) {
        String[] parts = date.split(":");

        OtpErlangObject[] dateObj = new OtpErlangObject[3];
        dateObj[0] = new OtpErlangInt(Integer.parseInt(parts[0]));
        dateObj[1] = new OtpErlangInt(Integer.parseInt(parts[1]));
        dateObj[2] = new OtpErlangInt(Integer.parseInt(parts[2]));

        OtpErlangTuple dateTuple = new OtpErlangTuple(dateObj);

        parts = time.split(":");

        dateObj = new OtpErlangObject[3];
        dateObj[0] = new OtpErlangInt(Integer.parseInt(parts[0]));
        dateObj[1] = new OtpErlangInt(Integer.parseInt(parts[1]));
        dateObj[2] = new OtpErlangInt(Integer.parseInt(parts[2]));

        OtpErlangTuple timeTuple = new OtpErlangTuple(dateObj);

        OtpErlangObject[] resultObject = new OtpErlangObject[2];
        resultObject[0] = dateTuple;
        resultObject[1] = timeTuple;

        return new OtpErlangTuple(resultObject);
    }
}