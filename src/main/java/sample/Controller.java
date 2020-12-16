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
            get_OID_response(OID_field.getText(), null, context);
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
            get_OID_response(".1.3.6.1.2.1.1.5.0", "Name: ",  context);   //name
            get_OID_response(".1.3.6.1.2.1.1.6.0", "Location: ", context);    //location
            get_OID_response(".1.3.6.1.2.1.25.1.1.0", "Uptime: ", context); //uptime
            get_OID_response("1.3.6.1.2.1.25.1.6.0", "Running Processes: ", context); //Processes
            get_OID_response(".1.3.6.1.2.1.1.1.0", "Hardware and Software: ", context);    //hardware
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            OID_field.setText(null);
        }
    }

    private void get_OID_response(String OID, String OID_description, SnmpContext context){
        SnmpResponse<VarbindCollection> response = context.get(OID);
        VarbindCollection result = response.get();
        if(OID_description == null){
            result_text_area.appendText(result.get(0).toString() + "\n");
        }else{
            result_text_area.appendText(OID_description + result.get(0).toString() + "\n");
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
}
