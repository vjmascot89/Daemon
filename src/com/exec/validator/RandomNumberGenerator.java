package com.exec.validator;

import java.util.Random;

public class RandomNumberGenerator {

	static public Integer generateRandom() {
		return Math.abs((int) (new Random().nextLong() % 1000000));
	}
//	public static void main(String ... arg){
//		String str=generateRandom().toString();
//		System.out.println(str);
//		System.out.println(str.hashCode());
//	}
}
