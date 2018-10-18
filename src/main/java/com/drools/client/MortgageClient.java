package com.drools.client;

import mortgages.mortgages.Applicant;
import mortgages.mortgages.IncomeSource;
import mortgages.mortgages.LoanApplication;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.command.CommandFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.*;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MortgageClient {

    private static final String USER = "kieserver";
    private static final String PASSWORD = "kieserver";
    private static final String URL = "http://localhost:8080/kie-server-7.12.0.Final-ee7/services/rest/server";

    private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;

    private KieServicesConfiguration conf;
    private KieServicesClient kieServicesClient;

    public void initialize() {
        conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
        conf.setMarshallingFormat(FORMAT);
        kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
    }

    public void listCapabilities() {
        KieServerInfo serverInfo = kieServicesClient.getServerInfo().getResult();
        System.out.print("Server capabilities:");
        for(String capability: serverInfo.getCapabilities()) {
            System.out.print(" " + capability);
        }
        System.out.println();
    }


    public void listContainers() {
        KieContainerResourceList containersList = kieServicesClient.listContainers().getResult();
        List<KieContainerResource> kieContainers = containersList.getContainers();
        System.out.println("Available containers: ");
        for (KieContainerResource container : kieContainers) {
            System.out.println("\t" + container.getContainerId() + " (" + container.getReleaseId() + ")");
        }
    }


    public void executeCommands() {

        List cmds = new ArrayList();

        Applicant applicant = new Applicant();
        applicant.setApplicationDate(new Date());
        applicant.setName("Jack");
        applicant.setAge(32);
        applicant.setCreditRating("OK");

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setAmount(25000);
        loanApplication.setDeposit(1500);
        loanApplication.setLengthYears(20);

        IncomeSource incomeSource = new IncomeSource();
        incomeSource.setAmount(30000);
        incomeSource.setType("Job");

        cmds.add( CommandFactory.newInsert( applicant, "applicant" ) );
        cmds.add( CommandFactory.newInsert( loanApplication, "application" ) );
        cmds.add( CommandFactory.newInsert( incomeSource, "incomeSource" ) );
        cmds.add(CommandFactory.newFireAllRules());


        System.out.println("== Sending commands to the server ==");
        RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);

        KieCommands commandsFactory = KieServices.Factory.get().getCommands();
        Command<?> batchCommand = commandsFactory.newBatchExecution(cmds);

        ServiceResponse<ExecutionResults> executeResponse = rulesClient.executeCommandsWithResults("mortgages", batchCommand);
        if(executeResponse.getType() == KieServiceResponse.ResponseType.SUCCESS) {
            System.out.println("Commands executed with success! Response: " + executeResponse.getResult());
            Applicant applicantDetails = (Applicant)executeResponse.getResult().getValue("applicant");
            System.out.println("Loan Application Status: " + ((true == applicantDetails.getApproved()) ? "APPROVED" : "REJECTED"));
        }
        else {
            System.out.println("Error executing rules. Message: ");
            System.out.println(executeResponse.getMsg());
        }
    }


    public static void main(String[] args) {
        MortgageClient mortgageClient = new MortgageClient();
        mortgageClient.initialize();
        mortgageClient.listCapabilities();
        mortgageClient.listContainers();
        mortgageClient.executeCommands();
    }
}
