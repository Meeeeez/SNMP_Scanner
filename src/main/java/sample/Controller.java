package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.soulwing.snmp.*;
import java.io.IOException;

public class Controller {

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

    private Mib mib;

    /*TODO: benutzer kann nicht in textareas schreiben,
            read by mib
            scan whole network

            OIDS:
                Uptime: .1.3.6.1.2.1.25.1.1.0
                Name: .1.3.6.1.2.1.1.5.0
                Processes: 1.3.6.1.2.1.25.1.6.0
                location: .1.3.6.1.2.1.1.6.0
                Hardware Info: .1.3.6.1.2.1.1.1.0
    */
    public void initialize() {
        scan_button.setDisable(true);
        network_textfield_whole.setPromptText("Enter Network address");
        mask_textfield_whole.setPromptText("Enter subnet mask");
        port_field_whole.getItems().addAll("161", "162");
        community_field_whole.getItems().addAll("public", "private");


        ip_address_field.setPromptText("Enter an IP-address");
        OID_field.setPromptText("Enter OID if using 'Scan'");
        port_field.getItems().addAll("161", "162");
        snmp_community_field.getItems().addAll("public", "private");

        OID_field.setOnKeyTyped(keyEvent -> scan_button.setDisable(false));
    }

    public void scanIP(ActionEvent actionEvent){
        mib = load_MIB();

        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
        target.setAddress(ip_address_field.getText());
        target.setPort(Integer.parseInt(port_field.getValue()));
        target.setCommunity(snmp_community_field.getValue());

        try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {
            String result_string = get_OID_response(OID_field.getText(), null, context);
            result_text_area.appendText(result_string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print_six_OID(ActionEvent actionEvent) {
        mib = load_MIB();

        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
        target.setAddress(ip_address_field.getText());
        target.setPort(Integer.parseInt(port_field.getValue()));
        target.setCommunity(snmp_community_field.getValue());

        try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {
            String result_string;
            result_string = get_OID_response(".1.3.6.1.2.1.1.5.0", "Name: ",  context);   //name
            result_text_area.appendText(result_string);
            result_string = get_OID_response(".1.3.6.1.2.1.1.6.0", "Location: ", context);    //location
            result_text_area.appendText(result_string);
            result_string = get_OID_response(".1.3.6.1.2.1.25.1.1.0", "Uptime: ", context); //uptime
            result_text_area.appendText(result_string);
            result_string = get_OID_response("1.3.6.1.2.1.25.1.6.0", "Running Processes: ", context); //Processes
            result_text_area.appendText(result_string);
            result_string = get_OID_response(".1.3.6.1.2.1.1.1.0", "Hardware and Software: ", context);    //hardware
            result_text_area.appendText(result_string);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            OID_field.setText(null);
        }
    }

    private String get_OID_response(String OID, String OID_description, SnmpContext context){
        SnmpResponse<VarbindCollection> response = context.get(OID);
        VarbindCollection result = response.get();
        if(OID_description == null){
            return result.get(0).toString() + "\n";
        }else{
            return OID_description + result.get(0).toString() + "\n";
        }
    }

    public void show_port_info(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SNMP Manager: Port Help");

        alert.setHeaderText("When do I use what?");
        alert.setContentText("161 is used when SNMP Managers communicate with SNMP Agents.\n162 is used when agents send unsolicited Traps to the SNMP Manager.");

        alert.setResizable(true);
        alert.getDialogPane().setPrefHeight(200);
        alert.getDialogPane().setPrefWidth(400);

        alert.showAndWait();
    }

    private Mib load_MIB(){
        Mib mib = MibFactory.getInstance().newMib();
        try {
            mib.load("SNMPv2-MIB");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mib;
    }

    public void scan_whole_network(ActionEvent actionEvent) {
        int mask = Integer.parseInt(mask_textfield_whole.getText());
        String IP_new;

        mib = load_MIB();

        if(mask == 32){
            SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
            target.setAddress(network_textfield_whole.getText());
            target.setPort(Integer.parseInt(port_field_whole.getValue()));
            target.setCommunity(community_field_whole.getValue());
            try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {
                String result_String = get_OID_response(".1.3.6.1.2.1.1.5.0", "Name: ",  context);   //name
                result_text_area_whole.appendText(result_String);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(mask == 24){
            String[] IP_address_arr = network_textfield_whole.getText().split("\\.");
            System.out.println(network_textfield_whole.getText());
            System.out.println(IP_address_arr.length);

            for (int i = 0; i < 255; i++){
                SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
                IP_new = IP_address_arr[0] + "." + IP_address_arr[1] + "." + IP_address_arr[2] + "." + i;
                System.out.println(IP_new);
                target.setAddress(IP_new);

                target.setPort(Integer.parseInt(port_field_whole.getValue()));
                target.setCommunity(community_field_whole.getValue());

                try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {
                    String result_String = get_OID_response(".1.3.6.1.2.1.1.5.0", "Name: ",  context);   //name
                    result_text_area_whole.appendText(IP_new + ": " + result_String);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
