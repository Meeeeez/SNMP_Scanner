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

## Running the Program

1. Create an Intellij JavaFX project
2. Add Maven support
3. Copy my pom.xml file
4. Copy my Main.java, Controller.java and sample.fxml files
5. Run the Program!
          
## Usage
- Scan a specific OID/MIB of an IP-adress
  - Enter your OID/MIB, IP-address, port and community string in the dedicated text fields and press "Scan using OID/MIB"
- Read SNMP information of an entered IP-address using six predefined OIDs
  - Enter an IP-adress, port and community string and press "Print 6 OIDs" (OID/MIB is not necessary)
- Scan the whole network
  - Enter an IP-adress, subnetmask without the "/"  (no VLSM, only enter 8, 16, 24, 32) , port and community string and press "Scan Network". It can take up to 30sec to scan an IP-address if there is nothing to be scanned. The name will be printed in the "SNMP-Responses" field if the IP-address has an SNMP-agent
- Receiving Traps/Informs
  - asdgasdgasdg  
