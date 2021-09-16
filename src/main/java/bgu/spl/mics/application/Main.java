package bgu.spl.mics.application;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** This is the Main class of the application. You should parse the input file, 
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static CountDownLatch leiaSignal; // a count down to when leia can start sending events
	public static void main(String[]args) {
		leiaSignal = new CountDownLatch(4); // wait until all 4 microservices have registered
		ProgramParameters parameters = null;
		Gson gson = null;
		/**
		 * create the class that represents the input
		 */
		try {
			gson = new Gson();
			Reader reader = Files.newBufferedReader(Paths.get(args[0]));
			parameters = gson.fromJson(reader,ProgramParameters.class);
			reader.close();
		}
		catch (Exception e)
		{

		}
		/**
		 * Create all microservices and threads according to the input
		 */
		MicroService leia = new LeiaMicroservice(parameters.getAttacks());
		MicroService han = new HanSoloMicroservice();
		MicroService r2 = new R2D2Microservice(parameters.getR2D2());
		MicroService c3 = new C3POMicroservice();
		MicroService lando = new LandoMicroservice(parameters.getLando());
		Ewoks ewoks = Ewoks.GetEwoksInstance();
		ewoks.addEwoks(parameters.getEwoks());
		Thread leiaThread = new Thread(leia);
		Thread hanThread = new Thread(han);
		Thread r2Thread = new Thread(r2);
		Thread c3Thread = new Thread(c3);
		Thread landoThread = new Thread(lando);

		leiaThread.start();
		r2Thread.start();
		landoThread.start();
		c3Thread.start();
		hanThread.start();

		try
		{
			leiaThread.join();
			r2Thread.join();
			landoThread.join();
			c3Thread.join();
			hanThread.join();
		}
		catch (Exception e)
		{

		}
		/**
		 * After all threads have finished we create the output file
		 */
		Diary diary = Diary.getInstance();
		try {
			Writer writer = Files.newBufferedWriter(Paths.get(args[1]));
			gson.toJson(diary,writer);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
