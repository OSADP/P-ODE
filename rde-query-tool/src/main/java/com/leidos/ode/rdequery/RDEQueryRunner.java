package com.leidos.ode.rdequery;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.leidos.ode.data.PodeDataDelivery;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.Scanner;

/**
 * Created by rushk1 on 6/9/2016.
 */
public class RDEQueryRunner {
    private static Logger log = LogManager.getLogger(RDEQueryRunner.class);
    public static void main(String[] args) {
        log.info("RDE Query Tool starting...");

        if (args.length < 1) {
            log.error("Not enough arguments, please specify query file.");
            log.info("Usage: java -jar rde-query-tool.jar <QUERY_FILE>");
            System.exit(1);
        }

        if (args.length == 2 && args[0].equals("-p")) {
            try (FileInputStream fis = new FileInputStream(args[1])) {
                log.info("Parsing data from file [" + args[1] + "]");
                Scanner s = new Scanner(fis);
                String datum = "";

                while (s.hasNextLine()) {
                    datum += s.nextLine();
                }

                PodeDataDelivery delivery = PodeParser.parsePodeDataDelivery(datum.replaceAll(" ", ""));
                Writer writer = new StringWriter();
                try {
                    JsonFactory factory = new JsonFactory();
                    JsonGenerator gen;
                    gen = factory.createGenerator(writer);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    mapper.addMixIn(Object.class, RemovePreparedDataMixin.class);
                    gen.setCodec(mapper);
                    gen.writeObject(delivery);
                } catch (IOException e) {
                    log.error("Unable to create JSON generator.");
                    System.exit(1);
                }

                System.out.println(writer.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load query from file
        String query = "";
        File f = new File (args[0]);
        try (FileInputStream fis = new FileInputStream(f)) {
            log.info("Loading query from file [" + f.getAbsolutePath() + "]");
            Scanner s = new Scanner(fis);

            while (s.hasNextLine()) {
                query += s.nextLine();
            }

            log.info("Loaded query: " + query);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ("".equals(query)) {
            log.error("Unable to load query from file [" + f.getAbsolutePath() + "] or file is empty.");
            System.exit(1);
        }

        QueryDataDistributor qdd = new QueryDataDistributor(query);

        qdd.run();
    }
}
