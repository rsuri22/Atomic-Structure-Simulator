package com.example.chickennugget;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private static Element createElement(String[] scrapedData){
        int atomicNumber = Integer.parseInt(scrapedData[0]);
        String name = scrapedData[1];
        String symbol = scrapedData[2];
        double atomicMass = Double.parseDouble(scrapedData[3]);
        int numberOfNeutrons = Integer.parseInt(scrapedData[4]);
        int numberOfProtons = Integer.parseInt(scrapedData[5]);
        int numberOfElectrons = Integer.parseInt(scrapedData[6]);
        int period = Integer.parseInt(scrapedData[7]);
        int group = Integer.parseInt(scrapedData[8]);
        String phase = scrapedData[9];
        String natural = scrapedData[11];
        String type = scrapedData[15];
        double atomicRadius = Double.parseDouble(scrapedData[16]);
        double electronegativity = Double.parseDouble(scrapedData[17]);
        double firstIonization = Double.parseDouble(scrapedData[18]);
        double density = Double.parseDouble(scrapedData[19]);
        double meltingPoint = Double.parseDouble(scrapedData[20]);
        double boilingPoint = Double.parseDouble(scrapedData[21]);
        int numberOfIsotopes = Integer.parseInt(scrapedData[22]);
        String discoverer = scrapedData[23];
        int numberOfValence = Integer.parseInt(scrapedData[27]);

        return new Element(atomicNumber,  name,  symbol,  atomicMass,  numberOfNeutrons,  numberOfProtons,  numberOfElectrons,
                period,  group,  phase,  natural,  type,  atomicRadius,  electronegativity,  firstIonization,
                density,  meltingPoint,  boilingPoint,  numberOfIsotopes,  discoverer,  numberOfValence);
    }

    public static List<Element> readElement(String filename){
        List<Element> elements = new ArrayList<>();
        Path pathToFile = Paths.get(filename);
        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {

            String line = br.readLine();

            while (line != null){
                String[] attributes = line.split(",");
                for (int i = 0; i < attributes.length; i++){
                    String s = attributes[i];
                    if (s.isEmpty()){
                        attributes[i] = "-1";
                    }
                }
                Element element = createElement(attributes);
                elements.add(element);
                line = br.readLine();
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        return elements;
    }
}
