package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.soulwing.snmp.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Controller extends Thread {

    public ChoiceBox<String> port_field;
    public ChoiceBox<String> snmp_community_field;
    public TextField ip_address_field;
    public TextField OID_field;
    public TextArea result_text_area;
    public Button scan_button;
    public TextArea event_log_text_area;

    public TextField network_textfield_whole;
    public TextField mask_textfield_whole;
    public ChoiceBox<String> port_field_whole;
    public ChoiceBox<String> community_field_whole;
    public TextArea result_text_area_whole;
    public Button trapsBtn;
    public Button six_oid_btn;

    private Mib mib;
    private String[] IP_address_arr;
    private int mask;

    public void initialize() {
        scan_button.setDisable(true);
        six_oid_btn.setDisable(true);

        //port_field.setValue("161");
        //snmp_community_field.setValue("public");

        network_textfield_whole.setPromptText("Network address");
        mask_textfield_whole.setPromptText("8, 16, 24 or 32");
        port_field_whole.getItems().addAll("161", "162");
        community_field_whole.getItems().addAll("public", "private");

        ip_address_field.setPromptText("Enter an IP-address");
        OID_field.setPromptText("Enter OID/MIB");
        port_field.getItems().addAll("161", "162");
        snmp_community_field.getItems().addAll("public", "private");

        OID_field.setOnKeyTyped(keyEvent -> scan_button.setDisable(false));
        ip_address_field.setOnKeyTyped(keyEvent -> six_oid_btn.setDisable(false));
        event_log_text_area.appendText(">Initialized Scanner\n");
    }

    public void scanIP(ActionEvent actionEvent){
        mib = load_MIB();

        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();

        setElements(target, null, false);

        try {
            SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);
            String result_string = get_OID_response(OID_field.getText(), "OID/MIB Response: ", context, false);
            result_text_area.appendText(result_string);
        } catch (Exception e) {
            if (e instanceof org.soulwing.snmp.SnmpException) {
                System.out.println("Error: No such Instance");
                event_log_text_area.appendText("Error: SNMP Target not found\n");
            }else {
                System.out.println("Error: Enter required information (IP, OID/MIB, Port)");
                event_log_text_area.appendText("Error: Enter required information (IP, OID/MIB, Port)\n");

            }
        }
    }

    public void print_six_OID(ActionEvent actionEvent) {
        mib = load_MIB();
        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();

        setElements(target, null, false);

        try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {
            String result_string;
            result_string = get_OID_response(".1.3.6.1.2.1.1.5.0", "Name: ",  context, true);   //name
            result_text_area.appendText(result_string);
            result_string = get_OID_response(".1.3.6.1.2.1.1.6.0", "Location: ", context, true);    //location
            result_text_area.appendText(result_string);
            result_string = get_OID_response(".1.3.6.1.2.1.25.1.1.0", "Uptime: ", context, true); //uptime
            result_text_area.appendText(result_string);
            result_string = get_OID_response("1.3.6.1.2.1.25.1.6.0", "Running Processes: ", context, true); //Processes
            result_text_area.appendText(result_string);
            result_string = get_OID_response(".1.3.6.1.2.1.1.1.0", "Hardware and Software: ", context, true);    //hardware
            result_text_area.appendText(result_string);
        } catch (Exception e) {
            System.out.println("Error: No such Instance found");
            event_log_text_area.appendText("Error: No such Instance found\n");
        }finally {
            OID_field.setText(null);
        }
    }

    private String get_OID_response(String OID, String OID_description, SnmpContext context, Boolean isCalledFromGetSix){
        SnmpResponse<VarbindCollection> response;
        boolean isOid = false;

        if (OID.contains(".")) isOid = true;

        if(isCalledFromGetSix || isOid){
            response = context.get(OID);
        }else {
            response = context.getNext(OID);
        }

        VarbindCollection result = response.get();
        event_log_text_area.appendText(">Requesting SNMP-Data\n");
        if(OID_description == null){
            if(result.get(0).toString() != null || result.get(0).toString().equals("")){
                event_log_text_area.appendText(">SNMP Response inbound\n");
                return result.get(0).toString() + "\n";
            }
            else
                event_log_text_area.appendText(">Error: No such instance found\n");
            return "Error: No Such Instance";
        }else{
            return OID_description + result.get(0).toString() + "\n";
        }
    }

    public void show_port_info(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SNMP Manager: Port Help");

        alert.setHeaderText("When do I use what?");
        alert.setContentText("161 is used when SNMP Managers communicate with SNMP Agents.\n162 is used when agents send Traps/Informs to the SNMP Manager.");

        alert.setResizable(true);
        alert.getDialogPane().setPrefHeight(200);
        alert.getDialogPane().setPrefWidth(400);

        alert.showAndWait();
    }

    private Mib load_MIB(){
        event_log_text_area.appendText(">Loading MIB...\n");
        Mib mib = MibFactory.getInstance().newMib();
        try {
            mib.load("SNMPv2-MIB");
        } catch (IOException e) {
            e.printStackTrace();
        }
        event_log_text_area.appendText(">MIB loaded successfully\n");
        return mib;
    }

    public void scan_whole_network(ActionEvent actionEvent) {
        event_log_text_area.appendText(">Scanning whole Network...\n");
        mask = Integer.parseInt(mask_textfield_whole.getText());

        mib = load_MIB();

        if(mask == 32){
            SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
            setElements(target, null, true);

            try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {
                String result_String = get_OID_response(".1.3.6.1.2.1.1.5.0", "",  context, false);   //name
                result_text_area_whole.appendText(target.getAddress() + " Name: " + result_String);
            } catch (Exception e) {
                System.out.println("Error: No such instance found");
                event_log_text_area.appendText(">Error: No such instance found\n");
            }
        }else if(mask == 24 || mask == 16 || mask == 8){
            IP_address_arr = network_textfield_whole.getText().split("\\.");
            this.start();
        }else {
            alertErr("mask");
        }
    }

    @Override
    public void run() {
        if(mask == 24){
            for (int i = 1; i < 255; i++){
                String IP_new = IP_address_arr[0] + "." + IP_address_arr[1] + "." + IP_address_arr[2] + "." + i;
                System.out.println(IP_new);
                getSNMPResponseWN(IP_new);
            }
        }else if(mask == 16){
            for (int j = 1; j < 255; j++){
                for (int i = 1; i < 255; i++){
                    String IP_new = IP_address_arr[0] + "." + IP_address_arr[1] + "." + j + "." + i;
                    getSNMPResponseWN(IP_new);
                }
            }
        }else if(mask == 8){
            for (int h = 1; h < 255; h++){
                for (int j = 1; j < 255; j++){
                    for (int i = 1; i < 255; i++){
                        String IP_new = IP_address_arr[0] + "." + h + "." + j + "." + i;
                        getSNMPResponseWN(IP_new);
                    }
                }
            }
        }
    }

    private void getSNMPResponseWN(String IP_new){
        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
        SnmpResponse<VarbindCollection> response;
        SnmpContext context;
        VarbindCollection result;
        String resPrinted;

        try{
            setElements(target, IP_new, true);
            context = SnmpFactory.getInstance().newContext(target, mib);
            response = context.get(".1.3.6.1.2.1.1.5.0");
            result = response.get();
            resPrinted = result.get(0).toString() + "\n";
            result_text_area_whole.appendText(IP_new + ": " + resPrinted);
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (Exception e) {
            System.out.println("Error: No such instance found");
            result_text_area_whole.appendText(IP_new + ": " + "not found\n");
        }
    }

    public void receiveTrapsInforms(ActionEvent actionEvent) {
        Mib mib;
        mib = load_MIB();
        event_log_text_area.appendText(">Waiting for Traps/Informs...\n");
        new Thread(() -> {
            SnmpListener listener = SnmpFactory.getInstance().newListener(10162, mib);
            try {
                listener.addHandler(event -> {
                    Platform.runLater(() -> {
                        result_text_area.appendText("Received: \n");

                        for (int i = 0; i < event.getSubject().getVarbinds().size(); i++) {
                            result_text_area.appendText(event.getSubject().getVarbinds().get(i).getName() + " : " + event.getSubject().getVarbinds().get(i));
                        }

                        result_text_area.appendText("");
                    });
                    return true;
                });
                Thread.sleep(60000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                listener.close();
            }
        }).start();
    }

    private void setElements(SimpleSnmpV2cTarget target, String newIP, Boolean isWholeNetwork){
        if(!isWholeNetwork){
            if (ip_address_field.getText().equals("")){
                alertErr("ip");
            }else {
                target.setAddress(ip_address_field.getText());
            }

            if(port_field.getValue() == null){
                alertErr("port");
            }else {
                target.setPort(Integer.parseInt(port_field.getValue()));
            }

            if (snmp_community_field.getValue() == null){
                alertErr("community");
            }else {
                target.setCommunity(snmp_community_field.getValue());
            }
        }else{
            if(port_field_whole.getValue() == null){
                alertErr("port");
            }else {
                target.setPort(Integer.parseInt(port_field_whole.getValue()));
            }

            if (community_field_whole.getValue() == null){
                alertErr("community");
            }else {
                target.setCommunity(community_field_whole.getValue());
            }

            if(network_textfield_whole.getText().equals("")){
                alertErr("ip");
            }else{
                if(newIP == null){
                    target.setAddress(network_textfield_whole.getText());
                }else {
                    target.setAddress(newIP);
                }
            }
        }

    }

    private void alertErr(String type){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("SNMP Manager: Enter additional info");

        switch (type) {
            case "port":
                alert.setHeaderText("Choose a Port before starting an SNMP request");
                event_log_text_area.appendText(">No Port specified\n");
                break;
            case "community":
                alert.setHeaderText("Set the community string before starting an SNMP request");
                event_log_text_area.appendText(">Community String not specified\n");
                break;
            case "ip":
                alert.setHeaderText("Enter an IP address before starting an SNMP request");
                event_log_text_area.appendText(">No IP specified\n");
                break;
            case "mask":
                alert.setHeaderText("Invalid subnetmask");
                event_log_text_area.appendText(">Invalid subnetmask\n");
                break;
        }

        alert.setResizable(true);
        alert.getDialogPane().setPrefHeight(100);
        alert.getDialogPane().setPrefWidth(400);

        alert.showAndWait();
    }
}