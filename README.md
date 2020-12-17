# SNMP Scanner with GUI

SNMP Scanner is a tool that reads information from an IP-address using SNMP/OIDs.

## Installation and Setup

Add the following libraries:

- log4j-over-slf4j-1.7.30.jar
- mibble-2.9.3.jar
- mibble-mibs-2.9.3.jar
- slf4j-api-1.7.30.jar
- snmp4j-2.8.4.jar
- tnm4j-1.0.11.jar

### Setup with Maven

Add the following dependencies to your pom.xml:
```xml
  <dependency>
    <groupId>org.soulwing.snmp</groupId>   <!-------SNMP Library----->
    <artifactId>tnm4j</artifactId>
    <version>1.0.11</version>
  </dependency>
  <dependency>
    <groupId>org.openjfx</groupId>         <!-------JavaFX Library----->
    <artifactId>javafx-fxml</artifactId>
    <version>11.0.1</version>
  </dependency>
```
Enter `mvn compile` to check that everything works correctly. 

### How to run the Code

1. Create an Intellij JavaFX project
2. Add Maven support
3. Copy my pom.xml file
4. Copy my Main.java and Controller.java classes
5. Run the Program!
          
## Usage
- Scan a specific OID of an IP-adress
  - Enter your OID and IP-adress in the provided text field and press "Scan using OID"
- Read information out of six predefined OIDs
  - Enter an IP-adress an press "Print 6 OIDs" (OID is not necessary)
- Scan the whole network (unfinished -> 32 mask working, 24 mask working partly)
  - Enter an IP-adress and a subnetmask without the "/" (no VLSM, only enter 8, 16, 24, 32) and press "Scan Network"
Your desired information will be printed in the field "SNMP-Responses". Event log is currently unavailable.
  #### Don't forget to enter Port and Community (if you need help with the port press "i") ####
  
