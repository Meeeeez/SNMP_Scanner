# SNMP Scanner with GUI

SNMP Scanner is a tool that reads SNMP information from an IP-address using OIDs/MIBs.

## Setup with Maven

Add the following dependencies to your pom.xml:
```xml
<dependencies>
        <dependency>
            <groupId>org.soulwing.snmp</groupId>
            <artifactId>tnm4j</artifactId>
            <version>1.0.11</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>11.0.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.30</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.30</version>
        </dependency>
    </dependencies>
```
Enter `mvn compile` to check whether everything works correctly. 

### Project Structure

Make sure your project structure looks like this (put `<sample.fxml>` in the resources folder):

<a href="https://drive.google.com/uc?export=view&id=1G159T_vL5_KIz5gMGvMAarxLmgLNDazY"><img src="https://drive.google.com/uc?export=view&id=1G159T_vL5_KIz5gMGvMAarxLmgLNDazY" style="width: 650px; max-width: 100%; height: auto" title="Click to enlarge picture" />

## Running the Program

1. Create an Intellij JavaFX project
2. Add Maven support
3. Copy my pom.xml file
4. Copy my Main.java, Controller.java and sample.fxml files
5. Run the Program!
          
## Usage
- Scan a specific OID/MIB of an IP-address
  - Enter your OID/MIB, IP-address, port and community string in the dedicated text fields and press "Scan using OID/MIB"
- Read SNMP information of an entered IP-address using six predefined OIDs
  - Enter an IP-adress, port and community string and press "Print 6 OIDs" (OID/MIB is not necessary)
- Scan the whole network
  - Switch to the "Scan Whole Network" Tab
  - Enter an IP-adress, a subnetmask without the "/"  (no VLSM, only enter 8, 16, 24, 32) , a port and your community string and click "Scan Network".
  - It can take up to 30 seconds to scan an IP-address if there is no SNMP-Agent running. Else the scan will take up to 2 seconds and the name of the system will be printed in the "SNMP-Responses" field.
  - Once you start an scan for a subnet you need to either wait until it finishes or restart the program to start a new network scan. If begin a network scan and start a new one (except /32) before the first one finishes, the program will continue with the first network scan. The readon why /32 networks still work is because it doesn't require a thread to run the scan.
- Receiving Traps/Informs
  - Click the button "Receive Traps/Informs" and wait for Traps/Informs.
