package com.example;

import java.io.*;
import java.util.*;

/**
 * FastA_Lang_Matthias
 * Author(s): Matthias Lang
 * Sequence Bioinformatics, WS 25/26
 */
public class FastA_Lang_Matthias {

	private static String getFastAContent(Collection<Pair> list){
		int charsInLine = 70;
		StringBuilder entries = new StringBuilder();

		for(Pair entry : list) {
			entries.append(entry.header).append(System.lineSeparator());

			StringBuilder chunkedSequence = new StringBuilder();
			int i;
			for (i = 0; i < entry.sequence.length() / charsInLine; i++) {
				chunkedSequence.append(entry.sequence, i * charsInLine, (i + 1) * charsInLine).append(System.lineSeparator());
			}
			chunkedSequence.append(entry.sequence.substring(i * charsInLine)).append(System.lineSeparator());

			entries.append(chunkedSequence);
		}

		return entries.toString();

	}

	public static void write(Collection<Pair> list, String fileName) throws IOException {

		String content = getFastAContent(list);
		if(fileName == null || fileName.isEmpty()) {
			System.out.println(content);
			return;
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
			bw.write(content);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static ArrayList<Pair> read(String fileName) throws IOException {
		var list = new ArrayList<Pair>();

		StringBuilder header = new StringBuilder();
		StringBuilder sequence = new StringBuilder();
		Runnable writeAndReset = () -> {
			list.add(new Pair(header.toString(), sequence.toString()));
			header.setLength(0);
			sequence.setLength(0);
		};
		try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
			Iterator<String> lineIterator = fileReader.lines().iterator();
			String line;
			do {
				line = lineIterator.next();
				if (line.startsWith(">")) {
					if (!header.isEmpty())
						writeAndReset.run();
					header.append(line.strip());
				} else {
					sequence.append(line.strip());
				}
			} while (lineIterator.hasNext());
			if (!header.isEmpty())
				writeAndReset.run();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return list;
	}

	/**
	 * a FastA record consisting of a pair of header and sequence
	 */
	public record Pair(String header, String sequence) {
	}
}
