# Java Program to execute Drools Rules on kie-server via REST api

## Set-up drools kie-server and kie-workbench

Download and configure kie-server and kie-workbench applications. Details about the set up can be found at [https://sompammar.github.io/drools/kie-server-workbench/setup/2018/10/16/drools-setup.html](https://sompammar.github.io/drools/kie-server-workbench/setup/2018/10/16/drools-setup.html)

## Import the sample mortgages 

-  Go to Projects page by clicking on ```Design - Create and modify projects```
- Click on Hamburger menu on the right side and click Try Samples
- Import ```Mortgages - Loan approval process automation...``` project
- Build and Deploy the project to kie-server

## Execute the application
    ```
    mvn compile exec:java -Dexec.mainClass="com.drools.client.MortgageClient"
    ```