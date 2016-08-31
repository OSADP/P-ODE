package com.leidos.ode.agent.parser.impl;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

/**
 * Created by rushk1 on 8/5/2016.
 */
public class SDCParserTest {

    @Test
    public void testParse() throws Exception {
        File inFile = new File("testdata/vsd.dat");
        Scanner s = new Scanner(new FileInputStream(inFile));

        String vsdData = s.nextLine();

        SDCParser parser = new SDCParser();

        parser.parse(vsdData.getBytes());
    }
}