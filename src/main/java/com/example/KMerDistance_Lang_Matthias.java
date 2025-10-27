package com.example;

import java.io.IOException;
import java.util.*;

/**
 * KMerDistance_Lang_Matthias.java
 * Author(s): Matthias Lang
 * Sequence Bioinformatics, WS 25/26
 */
public class KMerDistance_Lang_Matthias {
	/**
	 * run your code
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length !=2)
			throw new IllegalArgumentException("Wrong number of arguments");

		String fileName = args[0];

		int k = Integer.parseInt(args[1]);
		if(k < 1)
			throw new IllegalArgumentException("k can't be smaller than 1");

		ArrayList<FastA_Lang_Matthias.Pair> sequences;

		// read in FastA file
		try {
			sequences = FastA_Lang_Matthias.read(fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if(sequences.isEmpty())
			return;

		// for each sequence, extract all 8-mers and place into a set
		Map<FastA_Lang_Matthias.Pair, Set<String>> kMers = new HashMap<>();
		for(FastA_Lang_Matthias.Pair entry : sequences){
			kMers.put(entry, extractKMers(entry.sequence(), k));
		}

		System.out.println(kMers);

		// for each pair of sequences, compute the "Jaccard index" JI
		Map<DistancePair, Double> distances = new HashMap<>();
		for(int i = 0; i < sequences.size(); i++){
			for(int n = i; n < sequences.size(); n++){
				FastA_Lang_Matthias.Pair one = sequences.get(i);
				FastA_Lang_Matthias.Pair two = sequences.get(n);
				distances.put(new DistancePair(one, two), computeJaccardIndex(kMers.get(one), kMers.get(two)));
			}
		}

		System.out.println(distances);

		// Output the distances as matrix in the format (where n is the number of sequences):
		/*
		n
		name1 d11 d12 d13 ... d1n
		name2 d21 d22 ...
		...
		name_n dn1 dn2 ... dnn
		 */
		StringBuilder distanceMatrix = new StringBuilder();
		distanceMatrix.append(sequences.size()).append(System.lineSeparator());
		for(int i = 0; i < sequences.size(); i++){
			distanceMatrix.append(sequences.get(i).header());
			for(int n = 0; n < sequences.size(); n++){
				DistancePair key = new DistancePair(sequences.get(i), sequences.get(n));

				if(!distances.containsKey(key))
					key = new DistancePair(sequences.get(n), sequences.get(i));

				double distance = distances.get(key);
				distanceMatrix.append("\t").append(distance);
			}
			distanceMatrix.append(System.lineSeparator());
		}
		System.out.println(distanceMatrix.toString());

	}

	/**
	 * extract all k-mers
	 * @param sequence
	 * @param k
	 * @return
	 */
	public static Set<String> extractKMers(String sequence, int k) {
		Set<String> kMer = new HashSet<>();
		for(int i = 0; i < sequence.length()-k; i++){
			kMer.add(sequence.substring(i,i+k));
		}
		return kMer;
	}

	/**
	 * compute the Jaccard index
	 * @param set1
	 * @param set2
	 * @return Jaccard index
	 */
	public static double computeJaccardIndex (Set<String> set1,Set<String> set2) {
		Set<String> intersection = new HashSet<>(set1);
		intersection.retainAll(set2);
		if(intersection.isEmpty()){
			return -Math.log(100D);
		}

		Set<String> union = new HashSet<>(set1);
		union.addAll(set2);

		return -Math.log((double) intersection.size() /union.size());
	}

	/**
	 * a pair of 2 entries
	 */
	public record DistancePair(FastA_Lang_Matthias.Pair one, FastA_Lang_Matthias.Pair two) {
	}

}
