package com.example.chickennugget;

import javafx.application.Application;
import javafx.application.Platform;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Element> elements = CSVReader.readElement("/Users/rohansuri/Downloads/periodicdata2.csv");
        chemHelper(elements);
    }

    public static void chemHelper(List<Element> elements){
        Scanner sc = new Scanner(System.in);
        System.out.println("What do you need help with?\n\t1: Information about an element\n\t2: Molar Mass Calculation\n\t3: Structure Determination");
        String ans = sc.nextLine();
        boolean keepGoing = true;
        if (ans.equals("1")){
            while (keepGoing) {
                System.out.println("What element would you like information for? Enter atomic symbol please (type STOP to end): ");
                String symbol = sc.nextLine();
                if (symbol.equals("STOP") || symbol.equals(("stop")))
                    keepGoing = false;
                else {
                    System.out.println(Element.findBySymbolR(elements, symbol, 0));
                }
                System.out.println("Thanks for using my element finder!");
            }
        }

        else if (ans.equals("2")) {
            while (keepGoing) {
                System.out.println("What molecule would you like to find the molar mass of? Type STOP to end.");
                String userInput = sc.nextLine();
                if (userInput.equals("STOP") || userInput.equals("stop"))
                    keepGoing = false;
                else {
                    System.out.println(Element.calculateMolarMass(elements, userInput));
                }
            }
            System.out.println("Thanks for using my molar mass calculator!");
        }
        else if (ans.equals("3")) {
            while (keepGoing) {
                System.out.println("What molecule would you like to find the structure of (enter the molecule as AXBY n-, with n " +
                        "representing the charge on a molecule - if the charge is zero, don't add anything at the end). Type STOP to end.");
                String userInput = sc.nextLine();
                if (userInput.equals("STOP") || userInput.equals("stop"))
                    keepGoing = false;
                else if (userInput.toLowerCase().equals("nodemap")){
                    Application.launch(StructureBuilder.NodeMap.class);
                }
                else {
                    Element.structureDetermination(elements, userInput);
                }
            }
            System.out.println("Thanks for using my structure determinator!");
        }

        else
            System.out.println("Sorry, wrong answer. Goodbye!");
        sc.close();

    }
}
